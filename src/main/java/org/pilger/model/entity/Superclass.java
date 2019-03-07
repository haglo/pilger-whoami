package org.pilger.model.entity;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


@MappedSuperclass
public abstract class Superclass implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	@Column(name = "optlock", columnDefinition = "integer DEFAULT 0", nullable = false)
	private Long version = 0L;

	@GeneratedValue(generator = "UUID")
	@Type(type="uuid-char")
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator"
)
	private UUID puuid;

	private String comment;

	public Superclass() {
		this.prePersist();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getPuuid() {
		return puuid;
	}

	public void setPuuid(UUID puuid) {
		this.puuid = puuid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@PrePersist
	public void prePersist() {
		if (getPuuid() == null) {
			setPuuid(UUID.randomUUID());
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + " [uuid=" + puuid + "]";
	}

	@Override
	public int hashCode() {
		this.id = getId();
		return this.id != null ? this.id.hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final Superclass other = (Superclass) obj;

		this.puuid = getPuuid();
		other.puuid = other.getPuuid();

		return this.puuid != null && this.puuid.equals(other.puuid);
	}
}
