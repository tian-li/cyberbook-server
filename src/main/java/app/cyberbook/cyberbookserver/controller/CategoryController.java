package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.Category;
import app.cyberbook.cyberbookserver.model.CategoryDTO;
import app.cyberbook.cyberbookserver.model.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping()
    @ResponseBody
    public ResponseEntity<List<Category>> getCategories(@RequestParam(name = "userId") String userId) {
        List<Category> categoryList = categoryRepository.findAllByUserId(userId);
//        categoryList.forEach(c -> System.out.println("find by user id" + c));
        return ResponseEntity.ok(categoryRepository.findAllByUserId(userId));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") String id) {
        Optional<Category> category = categoryRepository.findById(id);

        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping()
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDTO value) {

        Category category = new Category();
        category.setUserId(value.getUserId());
        category.setSortOrder(value.getSortOrder());
        category.setName(value.getName());
        category.setIcon(value.getIcon());
        category.setColor(value.getColor());
        category.setIsSpend(value.getIsSpend());
        category.setAddedByUser(value.getAddedByUser());
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") String id, @RequestBody CategoryDTO value) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ResponseEntity("没有此记录", HttpStatus.BAD_REQUEST);
        }

        category.setSortOrder(value.getSortOrder());
        category.setName(value.getName());
        category.setIcon(value.getIcon());
        category.setColor(value.getColor());
        category.setIsSpend(value.getIsSpend());
        category.setAddedByUser(value.getAddedByUser());
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity.BodyBuilder deleteCategoryById(@PathVariable("id") String id) {
        try {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok();
        } catch (Exception e) {
            return ResponseEntity.badRequest();
        }
    }
}