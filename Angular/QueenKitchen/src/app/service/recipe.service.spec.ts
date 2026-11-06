import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RecipeService } from './recipe.service';
import { environment } from '../../environments/environment';

describe('RecipeService', () => {
  let service: RecipeService;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(RecipeService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('encodes filters and pagination', () => {
    service.list({ search: 'tomato soup', ingredient: 'garlic', page: 1, size: 9 }).subscribe();
    const request = http.expectOne(r => r.url === environment.apiUrl + '/recipes');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('search')).toBe('tomato soup');
    expect(request.request.params.get('ingredient')).toBe('garlic');
    expect(request.request.params.get('page')).toBe('1');
    request.flush({ content: [], totalElements: 0, totalPages: 0, number: 1 });
  });
  it('deletes through the protected recipes endpoint', () => {
    service.delete(12).subscribe();
    const request = http.expectOne(environment.apiUrl + '/recipes/12');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
