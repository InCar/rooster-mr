package com.incarcloud.rooster.entity;

import javax.persistence.Embeddable;

@Embeddable
public class MobileyeTsrPK extends MobileyePK {
    private int sn;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean equals(Object target){
        if(target.getClass() != MobileyeTsrPK.class) return false;

        MobileyeTsrPK pk = (MobileyeTsrPK)target;
        return super.equals(target) && sn == pk.sn;
    }

    public int hashCode(){
        return String.format("%s%s%d", vin==null?"0":vin, tm==null?"0":tm.toString(), sn).hashCode();
    }
}
