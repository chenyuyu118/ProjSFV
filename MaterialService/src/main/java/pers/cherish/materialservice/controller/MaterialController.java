package pers.cherish.materialservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.cherish.commons.cos.COSTemplate;
import pers.cherish.materialservice.domain.Material;
import pers.cherish.materialservice.repository.MaterialRepository;
import pers.cherish.response.MyResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/material")
public class MaterialController {

    private MaterialRepository materialRepository;
    private COSTemplate cosTemplate;

    @Autowired
    public void setMaterialRepository(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Autowired
    public void setCosClient(COSTemplate cosClient) {
        this.cosTemplate = cosClient;
    }

    @GetMapping("")
    ResponseEntity<MyResponse<List<Material>>> getAllMaterials(@RequestParam(name = "page", required = false, defaultValue = "0")int page) {
        Page<Material> all = materialRepository.findAll(PageRequest.of(page, 10));
        System.out.println(all.get().toList());
        return ResponseEntity.ok(MyResponse.ofData(all.stream().toList()));
    }

    @PostMapping("/{name}")
    ResponseEntity<MyResponse<String>> uploadMaterial(MultipartFile file,@PathVariable String name) {
        System.out.println(file.getOriginalFilename());
        String[] split = file.getOriginalFilename().split("\\.");
        String result;
        String key = null;
        Material material = new Material();
        material.setName(name);
        Material saved = materialRepository.save(material);
        try {
            File f = File.createTempFile(name, split[split.length-1]);
            file.transferTo(f);
            key = saved.getId() + "." + split[split.length - 1];
            result = cosTemplate.uploadMaterial(key, f);
        } catch (IOException e) {
            e.printStackTrace();
            result = "fault";
        }
        if (result.equals("fault")) {
            return ResponseEntity.status(500).body(MyResponse.ofMessage("错误"));
        } else  {
            return ResponseEntity.ok(MyResponse.ofMessage("成功"));
        }
    }
}
