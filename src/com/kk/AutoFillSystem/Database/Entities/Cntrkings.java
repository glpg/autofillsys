/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.Database.Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yi
 */
@Entity
@Table(name = "cntrkings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cntrkings.findAll", query = "SELECT c FROM Cntrkings c"),
    @NamedQuery(name = "Cntrkings.findById", query = "SELECT c FROM Cntrkings c WHERE c.id = :id"),
    @NamedQuery(name = "Cntrkings.findByTrkingNum", query = "SELECT c FROM Cntrkings c WHERE c.trkingNum = :trkingNum"),
    @NamedQuery(name = "Cntrkings.findByShipDate", query = "SELECT c FROM Cntrkings c WHERE c.shipDate = :shipDate"),
    @NamedQuery(name = "Cntrkings.findByDelivered", query = "SELECT c FROM Cntrkings c WHERE c.delivered = :delivered")})
public class Cntrkings implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "trking_num")
    private String trkingNum;
    @Basic(optional = false)
    @Column(name = "ship_date")
    @Temporal(TemporalType.DATE)
    private Date shipDate;
    @Basic(optional = false)
    @Column(name = "delivered")
    private boolean delivered;
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Addresses addressId;
    @JoinColumn(name = "carrier_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Carriers carrierId;
    @JoinColumn(name = "ustocntrking_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Ustocntrkings ustocntrkingId;

    public Cntrkings() {
    }

    public Cntrkings(Integer id) {
        this.id = id;
    }

    public Cntrkings(Integer id, String trkingNum, Date shipDate, boolean delivered) {
        this.id = id;
        this.trkingNum = trkingNum;
        this.shipDate = shipDate;
        this.delivered = delivered;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTrkingNum() {
        return trkingNum;
    }

    public void setTrkingNum(String trkingNum) {
        this.trkingNum = trkingNum;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public Addresses getAddressId() {
        return addressId;
    }

    public void setAddressId(Addresses addressId) {
        this.addressId = addressId;
    }

    public Carriers getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Carriers carrierId) {
        this.carrierId = carrierId;
    }

    public Ustocntrkings getUstocntrkingId() {
        return ustocntrkingId;
    }

    public void setUstocntrkingId(Ustocntrkings ustocntrkingId) {
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
        if (!(object instanceof Cntrkings)) {
            return false;
        }
        Cntrkings other = (Cntrkings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kk.AutoFillSystem.Database.Entities.Cntrkings[ id=" + id + " ]";
    }
    
}
