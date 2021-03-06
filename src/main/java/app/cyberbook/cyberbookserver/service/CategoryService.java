package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    public ResponseEntity<CyberbookServerResponse<List<Category>>> getCategories(HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        return ResponseEntity.ok(CyberbookServerResponse.successWithData(categoryRepository.findAllByUserId(user.getId())));
    }

    public ResponseEntity<CyberbookServerResponse<Category>> getCategoryById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Category> category = categoryRepository.findById(id);

        if (category.isPresent()) {
            if (user.getId().equals(category.get().getUserId())) {
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(category.get()));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CyberbookServerResponse<Category>> addCategory(CategoryDTO categoryDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Category category = createCategory(categoryDTO, user.getId());
        category.setAddedByUser(true);

        return ResponseEntity.ok(CyberbookServerResponse.successWithData(categoryRepository.save(category)));
    }

    public ResponseEntity<CyberbookServerResponse<Category>> updateCategory(String id, CategoryDTO categoryDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Category> findResult = categoryRepository.findById(id);

        if (findResult.isPresent()) {
            if (user.getId().equals(findResult.get().getUserId())) {
                Category category = findResult.get();
                category.setSortOrder(categoryDTO.getSortOrder());
                category.setName(categoryDTO.getName());
                category.setIcon(categoryDTO.getIcon());
                category.setColor(categoryDTO.getColor());
                category.setIsSpend(categoryDTO.getIsSpend());
                category.setAddedByUser(true);
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(categoryRepository.save(category)));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity deleteCategoryById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Category> category = categoryRepository.findById(id);

        if (category.isPresent()) {
            if (user.getId().equals(category.get().getUserId())) {
                categoryRepository.deleteById(id);
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(id));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public Category createCategory(CategoryDTO categoryDTO, String userId) {
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

    public void createCategories(List<CategoryDTO> categoryDTOs, String userId) {
        List<Category> categories = categoryDTOs.stream()
                .map(categoryDTO -> createCategory(categoryDTO, userId))
                .collect(Collectors.toList());

        categoryRepository.saveAll(categories);
    }

    public List<CategoryDTO> generateDefaultCategoryDTOs() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(
                new ClassPathResource("default-categories.json").getInputStream(),
                new TypeReference<List<CategoryDTO>>() {
                }
        );
    }

    public Boolean isCategoryPresent(String categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);

        return category.isPresent();
    }
}
