package com.walyCommerce.walycommerce.controllers;

import com.walyCommerce.walycommerce.dto.ProductDTO;
import com.walyCommerce.walycommerce.dto.ProductMinDTO;
import com.walyCommerce.walycommerce.dto.UriDto;
import com.walyCommerce.walycommerce.services.ProductService;
import com.walyCommerce.walycommerce.services.S3service;
import com.walyCommerce.walycommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.Servlet;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value ="/products")
public class ProductController {

    @Autowired
    private ProductService service;
    @GetMapping(value ="/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ProductMinDTO>> findAll(Pageable pageable, @RequestParam(name = "name", defaultValue = "") String name) {
        Page<ProductMinDTO> dto = service.findAll(name, pageable);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value ="/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,@Valid @RequestBody ProductDTO dto) {
        try{
            dto = service.update(id, dto);
            return ResponseEntity.ok(dto);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("recurso não encontrado");
        }

    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value ="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
       service.delete(id);
       return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/image")
    public ResponseEntity<UriDto> uploadImage(@RequestParam("file") MultipartFile file){
        UriDto dto = service.uploadFile(file);
        return ResponseEntity.ok(dto);
    }

}
