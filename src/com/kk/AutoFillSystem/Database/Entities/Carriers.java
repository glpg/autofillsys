/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Yi
 */
@Entity
@Table(name = "carriers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Carriers.findAll", query = "SELECT c FROM Carriers c"),
    @NamedQuery(name = "Carriers.findById", query = "SELECT c FROM Carriers c WHERE c.id = :id"),
    @NamedQuery(name = "Carriers.findByName", query = "SELECT c FROM Carriers c WHERE c.name = :name")})
public class Carriers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carrierId")
    private Collection<Cntrkings> cntrkingsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carrierId")
    private Collection<Ustrkings> ustrkingsCollection;

    public Carriers() {
    }

    public Carriers(Integer id) {
        this.id = id;
    }

    public Carriers(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public Collection<Cntrkings> getCntrkingsCollection() {
        return cntrkingsCollection;
    }

    public void setCntrkingsCollection(Collection<Cntrkings> cntrkingsCollection) {
        this.cntrkingsCollection = cntrkingsCollection;
    }

    @XmlTransient
    public Collection<Ustrkings> getUstrkingsCollection() {
        return ustrkingsCollection;
    }

    public void setUstrkingsCollection(Collection<Ustrkings> ustrkingsCollection) {
        this.ustrkingsCollection = ustrkingsCollection;
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
        if (!(object instanceof Carriers)) {
            return false;
        }
        Carriers other = (Carriers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kk.AutoFillSystem.Database.Entities.Carriers[ id=" + id + " ]";
    }
    
}
