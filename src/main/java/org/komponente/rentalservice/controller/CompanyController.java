package org.komponente.rentalservice.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.komponente.dto.client.ClientCreateDto;
import org.komponente.dto.client.ClientDto;
import org.komponente.dto.company.CompanyCreateDto;
import org.komponente.dto.company.CompanyDto;
import org.komponente.rentalservice.security.CheckSecurity;
import org.komponente.rentalservice.security.token.TokenService;
import org.komponente.rentalservice.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@AllArgsConstructor
@RestController
@RequestMapping("/company")
public class CompanyController {

    private CompanyService companyService;
    private TokenService tokenService;

    @ApiOperation(value = "Register company")
    @PostMapping("/register")
    public ResponseEntity<CompanyDto> registerCompany(@RequestBody @Valid CompanyCreateDto companyCreateDto)
    {
        return new ResponseEntity<>(companyService.createCompany(companyCreateDto), HttpStatus.CREATED);
    }

    @ApiOperation("Assign manager")
    @RequestMapping("/assign/{id}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<CompanyDto> assignCompany(@RequestHeader("Authorization") String authorization, @PathVariable Long id)
    {
        //TODO: kako se zove id u tokenu, jel malo ili veliko id?
        return new ResponseEntity<>(companyService.registerManagerToCompany(id, tokenService.parseToken(authorization).get("Id", Long.class)), HttpStatus.OK);
    }
}
