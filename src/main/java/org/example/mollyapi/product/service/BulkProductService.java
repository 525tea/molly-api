package org.example.mollyapi.product.service;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BulkProductService {

    List<Map<String,String>> saveBulkProducts(MultipartFile file, Long userId);
}
