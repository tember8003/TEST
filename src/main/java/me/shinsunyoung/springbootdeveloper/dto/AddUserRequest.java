package me.shinsunyoung.springbootdeveloper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 생성 Dto")
public class AddUserRequest {
    @NotBlank(message = "이메일은 반드시 입력해야 합니다.")
    private String email;
    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
    private String password;
}
