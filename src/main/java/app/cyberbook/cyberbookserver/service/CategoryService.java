package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.Category;
import app.cyberbook.cyberbookserver.model.CategoryDTO;
import app.cyberbook.cyberbookserver.model.CategoryRepository;
import app.cyberbook.cyberbookserver.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    public ResponseEntity<List<Category>> getCategories(HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        return ResponseEntity.ok(categoryRepository.findAllByUserId(user.getId()));
    }

    public ResponseEntity<Category> getCategoryById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Category> category = categoryRepository.findById(id);

        if (category.isPresent() && user.getId().equals(category.get().getUserId())) {
            return ResponseEntity.ok(category.get());
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

    }

    public ResponseEntity<Category> addCategory(CategoryDTO value, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);

        Category category = createCategory(value, user.getId());
        return ResponseEntity.ok(categoryRepository.save(category));
    }

//    public ResponseEntity<List<Category>> addCategories(List<CategoryDTO> categoryDTOs, HttpServletRequest req) {
//        User user = userService.getUserByHttpRequestToken(req);
//
//        List<Category> categories = createCategories(categoryDTOs, user.getId());
//        return ResponseEntity.ok(categoryRepository.saveAll(categories));
//    }

    public ResponseEntity<Category> updateCategory(String id, CategoryDTO value, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Category> findResult = categoryRepository.findById(id);

        if (findResult.isPresent() && user.getId().equals(findResult.get().getUserId())) {

            Category category = findResult.get();
            category.setSortOrder(value.getSortOrder());
            category.setName(value.getName());
            category.setIcon(value.getIcon());
            category.setColor(value.getColor());
            category.setIsSpend(value.getIsSpend());
            category.setAddedByUser(value.getAddedByUser());
            return ResponseEntity.ok(categoryRepository.save(category));
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity deleteCategoryById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Category> category = categoryRepository.findById(id);

        if (category.isPresent() && user.getId().equals(category.get().getUserId())) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok(id);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    Category createCategory(CategoryDTO categoryDTO, String userId) {
        Category category = new Category();
        category.setUserId(userId);
        category.setSortOrder(categoryDTO.getSortOrder());
        category.setName(categoryDTO.getName());
        category.setIcon(categoryDTO.getIcon());
        category.setColor(categoryDTO.getColor());
        category.setIsSpend(categoryDTO.getIsSpend());
        category.setAddedByUser(categoryDTO.getAddedByUser());
        return category;
    }

    public void createCategories(CategoryDTO[] categoryDTOs, String userId) {
        List<Category> categories = categoryDTOs.map(categoryDTO -> createCategory(categoryDTO, userId)).collect(Collectors.toList());
        categoryRepository.saveAll(categories);
    }

    public CategoryDTO[] generateDefaultCategoryDTOs() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        CategoryDTO[] categoryDTOs = objectMapper.readValue(new ClassPathResource("./default-categories.json").getFile(), CategoryDTO[].class);
        System.out.println("categoryDTOs " + categoryDTOs);
        return categoryDTOs;

    }

}
