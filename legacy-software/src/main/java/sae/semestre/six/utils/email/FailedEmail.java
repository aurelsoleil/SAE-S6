package sae.semestre.six.utils.email;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "failed_emails")
public class FailedEmail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "recipient", nullable = false)
    private String recipient;
    
    @Column(name = "subject", nullable = false)
    private String subject;
    
    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;
    
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
    
    @Column(name = "last_retry")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRetry;
    
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Date getLastRetry() {
        return lastRetry;
    }

    public void setLastRetry(Date lastRetry) {
        this.lastRetry = lastRetry;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
