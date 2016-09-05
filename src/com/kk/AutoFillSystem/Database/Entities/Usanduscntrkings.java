/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yi
 */
@Entity
@Table(name = "usanduscntrkings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usanduscntrkings.findAll", query = "SELECT u FROM Usanduscntrkings u"),
    @NamedQuery(name = "Usanduscntrkings.findById", query = "SELECT u FROM Usanduscntrkings u WHERE u.id = :id"),
    @NamedQuery(name = "Usanduscntrkings.findByUstrkingId", query = "SELECT u FROM Usanduscntrkings u WHERE u.ustrkingId = :ustrkingId"),
    @NamedQuery(name = "Usanduscntrkings.findByUstocntrkingId", query = "SELECT u FROM Usanduscntrkings u WHERE u.ustocntrkingId = :ustocntrkingId")})
public class Usanduscntrkings implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "ustrking_id")
    private int ustrkingId;
    @Basic(optional = false)
    @Column(name = "ustocntrking_id")
    private int ustocntrkingId;

    public Usanduscntrkings() {
    }

    public Usanduscntrkings(Integer id) {
        this.id = id;
    }

    public Usanduscntrkings(Integer id, int ustrkingId, int ustocntrkingId) {
        this.id = id;
        this.ustrkingId = ustrkingId;
        this.ustocntrkingId = ustocntrkingId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUstrkingId() {
        return ustrkingId;
    }

    public void setUstrkingId(int ustrkingId) {
        this.ustrkingId = ustrkingId;
    }

    public int getUstocntrkingId() {
        return ustocntrkingId;
    }

    public void setUstocntrkingId(int ustocntrkingId) {
        this.ustocntrkingId = ustocntrkingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usanduscntrkings)) {
            return false;
        }
        Usanduscntrkings other = (Usanduscntrkings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kk.AutoFillSystem.Database.Entities.Usanduscntrkings[ id=" + id + " ]";
    }
    
}
