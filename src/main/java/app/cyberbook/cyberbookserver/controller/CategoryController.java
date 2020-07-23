package app.cyberbook.cyberbookserver.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {

    @GetMapping()
    public Integer getCategories() {
        return 123;
    }

    @GetMapping(path = "{id}")
    public Integer getCategoryById(@PathVariable("id") String id) {
        return 123;
    }

    @PostMapping()
    public Integer createCategory(@RequestBody String value) {
        return 123;
    }

    @PutMapping(path = "{id}")
    public Integer updateCategory(@PathVariable("id") String id, @RequestBody String value) {
        return 123;
    }

    @DeleteMapping(path = "{id}")
    public Integer deleteCategoryById(@PathVariable("id") String id) {
        return 123;
    }
}
