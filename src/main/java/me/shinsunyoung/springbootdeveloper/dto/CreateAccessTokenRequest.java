package me.shinsunyoung.springbootdeveloper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "토큰 재발급 Dto")
public class CreateAccessTokenRequest {
    @NotBlank(message = "Refresh token은 반드시 입력해야 합니다.")
    private String refreshToken;
}