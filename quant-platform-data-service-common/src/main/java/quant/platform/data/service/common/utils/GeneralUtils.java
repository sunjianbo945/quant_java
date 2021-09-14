package quant.platform.data.service.common.utils;

public class GeneralUtils{

    public static boolean compare(Object o1, Object o2){
        if(o1==null && o2==null){
            return true;
        }else if(o1==null ||o2==null){
            return false;
        }else{
            return o1.equals(o2);
        }

    }

    public static boolean compareWithTolerance(Double o1, Double o2,double tolerance){
        if(o1==null && o2==null){
            return true;
        }else if(o1==null ||o2==null){
            return false;
        }else{
            return Math.abs(o1-o2)<tolerance;
        }

    }

    public static Double maxWithNull(Double d1, Double d2){
        if(d1 == null){
            return d2;
        }else if(d2 == null){
            return d1;
        }else{
            return Math.max(d1, d2);
        }
    }

    public static Double minWithNull(Double d1, Double d2){
        if(d1 == null){
            return d2;
        }else if(d2 == null){
            return d1;
        }else{
            return Math.min(d1, d2);
        }
    }

    public static Double addWithNull(Double d1, Double d2){
        if(d1 == null){
            return d2;
        }else if(d2 == null){
            return d1;
        }else{
            return d1 + d2;
        }
    }
}
