package sae.semestre.six.utils.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IFailedEmailDao extends JpaRepository<FailedEmail, Long> {
    
    @Query("SELECT fe FROM FailedEmail fe WHERE fe.retryCount < 3 ORDER BY fe.lastRetry ASC NULLS FIRST")
    List<FailedEmail> findEmailsToRetry();
    
}
