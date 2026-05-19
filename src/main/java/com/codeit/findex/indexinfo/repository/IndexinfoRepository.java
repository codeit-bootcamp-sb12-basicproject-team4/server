package com.codeit.findex.indexinfo.repository;

import com.codeit.findex.indexinfo.entity.Findex;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexinfoRepository extends JpaRepository<Findex, UUID> {
}
