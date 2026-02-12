package co.devskills.springbootboilerplate.repository;

import co.devskills.springbootboilerplate.entity.TransactionEntity;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findByAccountId(UUID accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.accountId = :accountId")
    Integer sumAmountByAccountId(UUID accountId);
}
