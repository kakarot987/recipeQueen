package com.QueensKitchenSpringBoot.controllers.recipeData;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String signupAndToken(String prefix) throws Exception {
        String username=prefix + UUID.randomUUID().toString().substring(0,8);
        String register=String.format("{\"username\":\"%s\",\"email\":\"%s@example.test\",\"password\":\"testPassword123\",\"role\":[\"admin\"]}",username,username);
        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(register))
            .andExpect(status().isOk());
        MvcResult login=mvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"username\":\"%s\",\"password\":\"testPassword123\"}",username)))
            .andExpect(status().isOk()).andReturn();
        JsonNode response=mapper.readTree(login.getResponse().getContentAsString());
        assertEquals("ROLE_USER",response.path("roles").get(0).asText(),
            "Public signup must not create administrators");
        return "Bearer " + response.get("accessToken").asText();
    }

    @Test void fullLifecycleAndOwnership() throws Exception {
        String token=signupAndToken("chef");
        String stranger=signupAndToken("guest");
        String body="{\"title\":\"Integration Tomato Soup\",\"category\":\"veg\",\"categoryDescription\":\"Fresh tomatoes\","
            +"\"servings\":2,\"prepMinutes\":10,\"cookMinutes\":20,\"published\":true,"
            +"\"recipeIngredientList\":[{\"name\":\"Tomato\",\"ingredientQuantity\":\"2 cups\"}],"
            +"\"recipeStepsList\":[{\"number\":\"1\",\"headline\":\"Simmer\",\"description\":\"Cook tomatoes\"}]}";
        mvc.perform(get("/api/recipes")).andExpect(status().isUnauthorized());
        MvcResult created=mvc.perform(post("/api/recipes").header("Authorization",token)
            .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andReturn();
        long id=mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get("/api/recipes").header("Authorization",stranger)
            .param("ingredient","tomato").param("category","veg")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(id));
        mvc.perform(put("/api/recipes/"+id).header("Authorization",stranger)
            .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        mvc.perform(delete("/api/recipes/"+id).header("Authorization",stranger))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/favorites/"+id).header("Authorization",stranger))
            .andExpect(status().isNoContent());
        mvc.perform(get("/api/favorites").header("Authorization",stranger))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(put("/api/recipes/"+id).header("Authorization",token)
            .contentType(MediaType.APPLICATION_JSON).content(body.replace("\"published\":true","\"published\":false")))
            .andExpect(status().isOk());
        mvc.perform(get("/api/recipes/"+id).header("Authorization",stranger))
            .andExpect(status().isNotFound());
        mvc.perform(get("/api/favorites").header("Authorization",stranger))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete("/api/recipes/"+id).header("Authorization",token))
            .andExpect(status().isNoContent());
    }

    @Test void rejectsEmptyOrInvalidData() throws Exception {
        String token=signupAndToken("valid");
        mvc.perform(post("/api/recipes").header("Authorization",token)
            .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/recipes").header("Authorization",token)
            .param("page","-1")).andExpect(status().isBadRequest());
    }

    @Test void groceriesPlannerRatingsAndRecipeMetadata() throws Exception {
        String token=signupAndToken("planner");
        String stranger=signupAndToken("outsider");
        String body="{\"title\":\"Breakfast oats\",\"category\":\"veg\",\"categoryDescription\":\"Healthy breakfast\","
            +"\"servings\":4,\"prepMinutes\":5,\"cookMinutes\":10,\"published\":true,"
            +"\"recipeIngredientList\":[{\"name\":\"Oats\",\"ingredientQuantity\":\"2 cups\"}],"
            +"\"recipeStepsList\":[{\"number\":\"1\",\"headline\":\"Cook\",\"description\":\"Heat gently\"}]}";
        MvcResult created=mvc.perform(post("/api/recipes").header("Authorization",token)
            .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.servings").value(4))
            .andExpect(jsonPath("$.prepMinutes").value(5))
            .andReturn();
        long id=mapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        String items="[{\"name\":\"Oats\",\"quantity\":\"2 cups\",\"checked\":true}]";
        mvc.perform(put("/api/shopping-list").header("Authorization",token)
            .contentType(MediaType.APPLICATION_JSON).content(items))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].checked").value(true));
        mvc.perform(get("/api/shopping-list").header("Authorization",token))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Oats"));
        mvc.perform(get("/api/shopping-list").header("Authorization",stranger))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

        mvc.perform(put("/api/meal-plan/2027-02-01/BREAKFAST").header("Authorization",token)
            .contentType(MediaType.APPLICATION_JSON).content("{\"recipeId\":" + id + "}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.recipeId").value(id));
        mvc.perform(get("/api/meal-plan").header("Authorization",token)
            .param("start","2027-02-01"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].recipeTitle").value("Breakfast oats"));
        mvc.perform(get("/api/meal-plan").header("Authorization",stranger)
            .param("start","2027-02-01"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

        mvc.perform(put("/api/recipes/"+id+"/reviews/mine").header("Authorization",stranger)
            .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":5,\"comment\":\"Delicious\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.rating").value(5));
        mvc.perform(get("/api/recipes/"+id+"/reviews").header("Authorization",token))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].comment").value("Delicious"));
        mvc.perform(put("/api/recipes/"+id+"/reviews/mine").header("Authorization",stranger)
            .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":6}"))
            .andExpect(status().isBadRequest());

        mvc.perform(delete("/api/recipes/"+id).header("Authorization",token))
            .andExpect(status().isNoContent());
        mvc.perform(get("/api/meal-plan").header("Authorization",token)
            .param("start","2027-02-01")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

}
