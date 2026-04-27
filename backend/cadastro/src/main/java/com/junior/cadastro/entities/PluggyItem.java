package com.junior.cadastro.entities;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_pluggy_item")
public class PluggyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pluggy_item_id", nullable = false, unique = true)
    private String pluggyItemId;

    private Instant createdAt;
    @Column(length = 30)
    private String syncStatus;

    @Column(length = 1000)
    private String lastSyncError;

    private Instant lastSyncAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PluggyItem() {
    }

 

    public PluggyItem(String pluggyItemId, User user) {
        this.pluggyItemId = pluggyItemId;
        this.user = user;
        this.createdAt = Instant.now();
    }
	public PluggyItem(Long id, String pluggyItemId, Instant createdAt, String syncStatus, String lastSyncError,
			Instant lastSyncAt, User user) {
		super();
		this.id = id;
		this.pluggyItemId = pluggyItemId;
		this.createdAt = createdAt;
		this.syncStatus = syncStatus;
		this.lastSyncError = lastSyncError;
		this.lastSyncAt = lastSyncAt;
		this.user = user;
	}




	public Long getId() {
        return id;
    }

    public String getPluggyItemId() {
        return pluggyItemId;
    }

    public void setPluggyItemId(String pluggyItemId) {
        this.pluggyItemId = pluggyItemId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public String getSyncStatus() {
		return syncStatus;
	}


	public void setSyncStatus(String syncStatus) {
		this.syncStatus = syncStatus;
	}


	public String getLastSyncError() {
		return lastSyncError;
	}


	public void setLastSyncError(String lastSyncError) {
		this.lastSyncError = lastSyncError;
	}


	public Instant getLastSyncAt() {
		return lastSyncAt;
	}


	public void setLastSyncAt(Instant lastSyncAt) {
		this.lastSyncAt = lastSyncAt;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PluggyItem other = (PluggyItem) obj;
		return Objects.equals(id, other.id);
	}
    
    
}
