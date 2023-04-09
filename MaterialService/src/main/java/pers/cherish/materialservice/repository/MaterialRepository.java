package pers.cherish.materialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pers.cherish.materialservice.domain.Material;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
