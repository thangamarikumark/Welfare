package education.scheme.welfare.repository;

import education.scheme.welfare.models.WelfareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WelfareRepository extends JpaRepository<WelfareEntity, Long> {
}