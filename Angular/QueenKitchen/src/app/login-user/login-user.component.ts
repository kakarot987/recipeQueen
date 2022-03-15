import { Component, OnInit ,ViewEncapsulation} from '@angular/core';
import { TokenStorageService } from "../service/token-storage.service";
import { ImageService } from "../service/image.service";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RecipeIngredient } from "src/assets/ingredientRecipe";
import { RecipeSteps } from "src/assets/recipeSteps";
import { RecipeService } from "../service/recipe.service";
import { RecipeResponse } from "src/assets/recipeResponse";

class ImageSnippet {
 
  constructor(public src: string, public file: File) {}
}


@Component({
  selector: 'app-login-user',
  templateUrl: './login-user.component.html',
  styleUrls: ['./login-user.component.css'],
  encapsulation: ViewEncapsulation.None,
  styles: [`
    .dark-modal .modal-content {
      background-color: #292b2c;
      color: white;
    }
    .dark-modal .close {
      color: white;
    }
    .light-blue-backdrop {
      background-color: #5cb3fd;
    }
  `]
})
export class LoginUserComponent implements OnInit {

  recipeIngredient : Array<RecipeIngredient> = []

  recipeSteps : Array<RecipeSteps> = []

  constructor(private tokenStorage:TokenStorageService,
    private modalService: NgbModal,
    private imageService:ImageService,
    private recipeService: RecipeService) {
  }

  recipeResponse : Array<RecipeResponse> = []
  stringifiedData: any;  
  parsedJson: any; 
  ngOnInit(): void {
    this.userId = this.tokenStorage.getUser().id;
    console.log(this.userId)
    this.recipeService.getrecipe(this.userId).subscribe(
      data =>{
        console.log(data.response)
        this.stringifiedData = JSON.stringify(data);  
        this.parsedJson = JSON.parse(this.stringifiedData);
        console.log(this.parsedJson)
        for (let index = 0; index < this.parsedJson.length; index++) {
          console.log(this.parsedJson[index].category)
          console.log(this.parsedJson[index].userId)
          console.log(this.parsedJson[index].title)
          console.log(this.parsedJson[index].recipeDescription)
          console.log("---------",this.parsedJson[index].recipeIngredientList)
        console.log("---------S",this.parsedJson[index].recipeSteps[0].headline)
        }
      }
    )
  }
  selectedFile !: ImageSnippet;

  processFile(imageInput: any) {
    console.log("inside image Input function")
    const file: File = imageInput.files[0];
    const reader = new FileReader();

    reader.addEventListener('load', (event: any) => {

      this.selectedFile = new ImageSnippet(event.target.result, file);

      this.imageService.uploadProfilePic(this.selectedFile.file).subscribe(
        (res) => {
        
        },
        (err) => {
        
        })
    });

    reader.readAsDataURL(file);
  }
  openchangeProfileModalCustomClass(changeProfileContent:any) {
    this.modalService.open(changeProfileContent, {backdropClass: 'light-blue-backdrop'});
  }

  addRecipeModal(addRecipeContent:any) {
    this.modalService.open(addRecipeContent, {backdropClass: 'light-blue-backdrop'});
  }


  recipeTitle !: string
  recipeshortDiscription !: string
  recipeCategory !: string

  onSubmitAddIntro(data:any){
    this.recipeTitle = data.recipeTitle;
    this.recipeshortDiscription = data.recipeshortDiscription;
    console.log(data.recipeTitle , data.recipeshortDiscription)
    console.log(this.nonVeg , this.veg)
  } 

  veg : boolean = false
  nonVeg : boolean = false
  getCheckboxesValueVeg(data : any) {
    this.veg = true
    this.nonVeg = false
  }
  getCheckboxesValueNonVeg(data:any){
    this.veg = false
    this.nonVeg = true
  }

  userId !: any
  addRecipeFinal(){
    console.log("-----userId",this.tokenStorage.getUser().id)
    console.log("inside")
    console.log("------------",this.recipeIngredient);
    console.log("------------",this.recipeSteps);
    console.log("------------",this.recipeshortDiscription);
    console.log("------------",this.recipeTitle);
    if(this.veg == true){
      this.recipeCategory = "veg"
    } else if(this.veg == false){
      this.recipeCategory = "non-veg"
    }
    console.log("------------",this.recipeCategory);
    this.recipeService.addrecipe(this.userId,this.recipeTitle,
      this.recipeCategory,this.recipeshortDiscription,this.recipeIngredient,
      this.recipeSteps).subscribe(
        data => {
          console.log("------Data Here After success");
          this.ngOnInit();
        }
        ,
        error =>{
          console.log(error);
        }
      )
  }

  onSubmitAddRecipe(data:any){
    let recipeIng = new RecipeIngredient();
    window.sessionStorage.setItem("ingredientName",data.ingredientName);
    window.sessionStorage.setItem("ingredientQuantity",data.ingredientQuantity)

    recipeIng.name = (sessionStorage.getItem("ingredientName") || '{}')
    recipeIng.ingredientQuantity = (sessionStorage.getItem("ingredientQuantity")|| '{}')

    this.recipeIngredient.push(recipeIng);
    data.ingredientName = " "
    data.ingredientQuantity = " "
    console.log("list here is" + this.recipeIngredient.length)
  }

  editingredient(data:any){
    console.log("inside here",data);
    //const index = this.recipeIngredient.findIndex(ingre => ingre === data)
    //console.log("inside here",index);
    this.recipeIngredient.forEach((value,index) =>{
      if(value.name == data) this.recipeIngredient.splice(index,1);
    })
  }

  onSubmitRecipeSteps(data:any){
    let steps = new RecipeSteps();
    window.sessionStorage.setItem("stepNumber",data.stepNumber)
    window.sessionStorage.setItem("stepHeadline",data.stepHeadline)
    window.sessionStorage.setItem("stepDescription",data.stepDescription)

    steps.number = (window.sessionStorage.getItem("stepNumber") || '{}')
    steps.headline = (window.sessionStorage.getItem("stepHeadline") || '{}')
    steps.description = (window.sessionStorage.getItem("stepDescription") || '{}')

    this.recipeSteps.push(steps)
  }

  editSteps(data:any){
    this.recipeSteps.forEach((value,index) =>{
      if(value.number == data) this.recipeSteps.splice(index,1);
    })
  }

  logout(): void {
    console.log("inside logout")
    this.tokenStorage.signOut();
    this.reloadPage();
  }
  reloadPage(): void {
    window.location.reload();
  }
}
