package pers.cherish.materialservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_material")
public class Material {

    private String name;

    @Id
    @jakarta.persistence.Id
    @UuidGenerator
    private String id;
}
