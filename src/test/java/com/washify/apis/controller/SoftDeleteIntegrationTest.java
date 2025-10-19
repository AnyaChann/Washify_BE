package com.washify.apis.controller;

import com.washify.apis.entity.User;
import com.washify.apis.repository.UserRepository;
import com.washify.apis.service.SoftDeleteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho Soft Delete functionality
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SoftDeleteIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SoftDeleteService softDeleteService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Tạo test user
        testUser = new User();
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setPhone("0901234567");
        testUser.setAddress("Test Address");
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);
    }
    
    @Test
    void testSoftDelete_ShouldSetDeletedAt() {
        // When: Xóa mềm user
        userRepository.delete(testUser);
        userRepository.flush();
        
        // Then: User không còn trong findAll() (vì @Where clause)
        List<User> activeUsers = userRepository.findAll();
        assertThat(activeUsers).doesNotContain(testUser);
        
        // But: User vẫn tồn tại trong DB với deleted_at
        List<User> deletedUsers = softDeleteService.getDeletedUsers();
        assertThat(deletedUsers).hasSize(1);
        assertThat(deletedUsers.get(0).getId()).isEqualTo(testUser.getId());
        assertThat(deletedUsers.get(0).getDeletedAt()).isNotNull();
    }
    
    @Test
    void testGetDeletedUsers_ShouldReturnOnlyDeletedRecords() throws Exception {
        // Given: User đã bị xóa mềm
        userRepository.delete(testUser);
        
        // When: Gọi API get deleted users
        mockMvc.perform(get("/api/soft-delete/users/deleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(testUser.getId()));
    }
    
    @Test
    void testRestoreUser_ShouldSetDeletedAtToNull() throws Exception {
        // Given: User đã bị xóa mềm
        userRepository.delete(testUser);
        userRepository.flush();
        
        // Verify user đã bị ẩn
        List<User> activeUsers = userRepository.findAll();
        assertThat(activeUsers).doesNotContain(testUser);
        
        // When: Khôi phục user
        mockMvc.perform(put("/api/soft-delete/users/" + testUser.getId() + "/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User đã được khôi phục thành công"));
        
        // Then: User quay lại danh sách active
        activeUsers = userRepository.findAll();
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getId()).isEqualTo(testUser.getId());
        
        // And: deleted_at = NULL
        List<User> deletedUsers = softDeleteService.getDeletedUsers();
        assertThat(deletedUsers).isEmpty();
    }
    
    @Test
    void testRestoreNonDeletedUser_ShouldReturnError() throws Exception {
        // Given: User chưa bị xóa (deleted_at = NULL)
        
        // When: Cố gắng restore user chưa xóa
        mockMvc.perform(put("/api/soft-delete/users/" + testUser.getId() + "/restore"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void testPermanentDelete_ShouldRemoveFromDatabase() throws Exception {
        // Given: User đã bị xóa mềm
        userRepository.delete(testUser);
        userRepository.flush();
        Long userId = testUser.getId();
        
        // Verify user vẫn tồn tại trong deleted list
        List<User> deletedUsers = softDeleteService.getDeletedUsers();
        assertThat(deletedUsers).hasSize(1);
        
        // When: Xóa vĩnh viễn user
        mockMvc.perform(delete("/api/soft-delete/users/" + userId + "/permanent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        // Then: User không còn tồn tại ở bất kỳ đâu
        deletedUsers = softDeleteService.getDeletedUsers();
        assertThat(deletedUsers).isEmpty();
        
        assertThat(userRepository.findById(userId)).isEmpty();
    }
    
    @Test
    void testFindAll_ShouldNotReturnDeletedRecords() {
        // Given: Có 2 users, 1 active và 1 deleted
        User activeUser = new User();
        activeUser.setFullName("Active User");
        activeUser.setEmail("active@example.com");
        activeUser.setPassword("password");
        activeUser = userRepository.save(activeUser);
        
        userRepository.delete(testUser); // Xóa mềm testUser
        
        // When: Gọi findAll()
        List<User> users = userRepository.findAll();
        
        // Then: Chỉ trả về active user
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getId()).isEqualTo(activeUser.getId());
    }
    
    @Test
    void testFindById_DeletedUser_ShouldReturnEmpty() {
        // Given: User đã bị xóa mềm
        Long userId = testUser.getId();
        userRepository.delete(testUser);
        
        // When: Tìm user bằng findById()
        var result = userRepository.findById(userId);
        
        // Then: Không tìm thấy (vì @Where clause)
        assertThat(result).isEmpty();
    }
    
    @Test
    void testFindByEmailIncludingDeleted_ShouldFindDeletedUser() {
        // Given: User đã bị xóa mềm
        String email = testUser.getEmail();
        userRepository.delete(testUser);
        
        // When: Tìm user bằng custom query bypass @Where
        var result = userRepository.findByEmailIncludingDeleted(email);
        
        // Then: Tìm thấy user dù đã bị xóa
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getDeletedAt()).isNotNull();
    }
    
    @Test
    void testSoftDeleteWithRelationships_ShouldPreserveData() {
        // TODO: Test với entities có relationships (User -> Orders)
        // Verify rằng khi xóa mềm User, Orders vẫn còn
    }
    
    @Test
    void testBulkRestore_ShouldRestoreMultipleRecords() {
        // TODO: Test restore nhiều records cùng lúc
    }
    
    @Test
    void testAutoCleanup_ShouldDeleteOldRecords() {
        // TODO: Test scheduled job xóa records cũ > 30 ngày
    }
}
