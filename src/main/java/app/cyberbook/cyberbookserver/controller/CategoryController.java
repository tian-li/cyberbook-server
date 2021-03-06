package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.Category;
import app.cyberbook.cyberbookserver.model.CategoryDTO;
import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<CyberbookServerResponse<List<Category>>> getCategories(HttpServletRequest req) {
        return categoryService.getCategories(req);
    }

    @PostMapping()
    public ResponseEntity<CyberbookServerResponse<Category>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, HttpServletRequest req) {
        return categoryService.addCategory(categoryDTO, req);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<CyberbookServerResponse<Category>> getCategoryById(@PathVariable("id") String id, HttpServletRequest req) {
        return categoryService.getCategoryById(id, req);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<CyberbookServerResponse<Category>> updateCategory(@PathVariable("id") String id, @RequestBody CategoryDTO value, HttpServletRequest req) {
        return categoryService.updateCategory(id, value, req);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteCategoryById(@PathVariable("id") String id, HttpServletRequest req) {
        return categoryService.deleteCategoryById(id, req);
    }
}