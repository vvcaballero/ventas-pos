package com.pos.ventas.service;

import com.pos.ventas.dto.LoginRequestDTO;
import com.pos.ventas.dto.ResponseDTO;

public interface AuthService {
    ResponseDTO login(LoginRequestDTO request);
}
