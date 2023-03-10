package pers.cherish.videoservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.cherish.commons.cos.COSTemplate;

@SpringBootTest
public class COSTest {

    private COSTemplate cosTemplate;

    @Autowired
    public void setCosTemplate(COSTemplate cosTemplate) {
        this.cosTemplate = cosTemplate;
    }

}
