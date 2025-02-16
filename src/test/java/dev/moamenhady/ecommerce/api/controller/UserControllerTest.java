package dev.moamenhady.ecommerce.api.controller;

import dev.moamenhady.ecommerce.api.model.dto.UserAuthDto;
import dev.moamenhady.ecommerce.api.service.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRegister_Success() throws Exception {
        // Arrange
        UserAuthDto request = new UserAuthDto("test@example.com", "password");
        String token = "jwtToken";

        when(userService.register(request.email(), request.password())).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(token));

        verify(userService, times(1)).register(request.email(), request.password());
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        UserAuthDto request = new UserAuthDto("test@example.com", "password");
        String token = "jwtToken";

        when(userService.authenticate(request.email(), request.password())).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(userService, times(1)).authenticate(request.email(), request.password());
    }

    @Test
    public void testUpdateName_Success() throws Exception {
        // Arrange
        String name = "newName";

        doNothing().when(userService).update(name);

        // Act & Assert
        mockMvc.perform(put("/api/users/update")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(content().string("Name updated successfully"));

        verify(userService, times(1)).update(name);
    }
}