package com.example.springprojectmanager.dtos;

import java.util.List;

public record ErroResponse(String msg, List<CampoErro> campoErroList) {
}
