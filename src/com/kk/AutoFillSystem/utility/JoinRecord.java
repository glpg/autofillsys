/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.utility;

import com.kk.AutoFillSystem.Database.Entities.Cntrkings;
import com.kk.AutoFillSystem.Database.Entities.Orders;
import com.kk.AutoFillSystem.Database.Entities.Trklines;
import com.kk.AutoFillSystem.Database.Entities.Ustocntrkings;
import com.kk.AutoFillSystem.Database.Entities.Ustrkings;

/**
 *
 * @author Yi
 */
public class JoinRecord {
    private Orders order;
    private Ustrkings usTrk;
    private Trklines trkline;
    private Ustocntrkings intlTrk;
    private Cntrkings cnTrk;
    
    public JoinRecord() {
        
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Ustrkings getUsTrk() {
        return usTrk;
    }

    public void setUsTrk(Ustrkings usTrk) {
        this.usTrk = usTrk;
    }

    public Ustocntrkings getIntlTrk() {
        return intlTrk;
    }

    public void setIntlTrk(Ustocntrkings intlTrk) {
        this.intlTrk = intlTrk;
    }

    public Cntrkings getCnTrk() {
        return cnTrk;
    }

    public void setCnTrk(Cntrkings cnTrk) {
        this.cnTrk = cnTrk;
    }

    public Trklines getTrkline() {
        return trkline;
    }

    public void setTrkline(Trklines trkline) {
        this.trkline = trkline;
    }
    
    
    
    
}
