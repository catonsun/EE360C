/*
 * Name: Anthony Weems
 * EID: amw3647
 */

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map;
import java.util.Comparator;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the
     * Stable Matching problem. Study the description of a Matching in the
     * project documentation to help you with this.
     */

    /*
     * We say that an assignment of tenants to apartments (a matching), S, is
     * weakly stable unless there exists a tenant T and an apartment A such
     * that each of T and (the landlord owning) A strictly prefers the other
     * to their partner in S. In other words, a match is weakly stable if
     * there are entities who are indifferent to whether they are paired
     * with their current partners, or with each other, but not weakly
     * stable if they prefer each other. 
     */
    public boolean isStableMatching(Matching m) {
        if (m.getTenantCount() != m.getTenantMatching().size()) {
            return false;
        }

        HashMap<Integer, Integer> apartmentTable = new HashMap<Integer, Integer>();
        for (int lord = 0; lord < m.getLandlordCount(); lord++) {
            for (Integer e : m.getLandlordOwners().get(lord)) {
                apartmentTable.put(e, lord);
            }
        }
        HashMap<Integer, Integer> tenantTable = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> aptTable = new HashMap<Integer, Integer>();
        for (int tenant = 0; tenant < m.getTenantCount(); tenant++) {
            int apt = m.getTenantMatching().get(tenant);
            tenantTable.put(tenant, apt);
            aptTable.put(apt, tenant);
        }

        for (int tenant = 0; tenant < m.getTenantCount(); tenant++) {
            ArrayList<Integer> mprefs = m.getTenantPref().get(tenant);
            int mapt = m.getTenantMatching().get(tenant);
            int mpref = mprefs.get(mapt);
            int mlord = apartmentTable.get(mapt);
            ArrayList<Integer> prospects = new ArrayList<Integer>();
            for (int apt = 0; apt < m.getTenantCount(); apt++) {
                if (mprefs.get(apt) < mpref) {
                    prospects.add(apartmentTable.get(apt));
                }
            }
            for (Integer lord : prospects) {
                ArrayList<Integer> prefs = m.getLandlordPref().get(lord);
                ArrayList<Integer> apts = m.getLandlordOwners().get(lord);

                for (Integer apt : apts) {
                    if (prefs.get(tenant) < prefs.get(aptTable.get(apt))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public class Pair {
        public final Integer x;
        public final Integer y;
        public Pair(Integer x, Integer y) { 
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Determines a solution to the Stable Matching problem from the given input
     * set. Study the project description to understand the variables which
     * represent the input to your solution.
     * 
     * @return A stable Matching.
     */
    public Matching stableMatchingGaleShapley(Matching m) {
        /* convert preference lists from partial to total order */
        Map<Integer, List<Integer>> tenantToApartment = new TreeMap<Integer, List<Integer>>();
        for (int tenant = 0; tenant < m.getTenantCount(); tenant++) {
            ArrayList<Integer> tenantPref = m.getTenantPref().get(tenant);
            TreeSet<Pair> prefSet = new TreeSet<Pair>(new Comparator<Pair>() {
                public int compare(Pair a, Pair b) {
                    int cmp = a.y.compareTo(b.y);
                    return (cmp == 0) ? -1 : cmp;
                }
            });
            for (int apt = 0; apt < m.getTenantCount(); apt++) {
                prefSet.add(new Pair(apt, tenantPref.get(apt)));
            }
            List<Integer> pref = new ArrayList<Integer>();
            for (Pair e : prefSet) {
                pref.add(e.x);
            }
            tenantToApartment.put(tenant, pref);
        }

        /* convert landlord preferences to apartment total order */
        Map<Integer, List<Integer>> apartmentToTenant = new TreeMap<Integer, List<Integer>>();
        for (int lord = 0; lord < m.getLandlordCount(); lord++) {
            ArrayList<Integer> lordPref = m.getLandlordPref().get(lord);
            TreeSet<Pair> prefSet = new TreeSet<Pair>(new Comparator<Pair>() {
                public int compare(Pair a, Pair b) {
                    int cmp = a.y.compareTo(b.y);
                    return (cmp == 0) ? -1 : cmp;
                }
            });
            for (int tenant = 0; tenant < m.getTenantCount(); tenant++) {
                prefSet.add(new Pair(tenant, lordPref.get(tenant)));
            }
            List<Integer> pref = new ArrayList<Integer>();
            for (Pair e : prefSet) {
                pref.add(e.x);
            }
            for (Integer apt : m.getLandlordOwners().get(lord)) {
                apartmentToTenant.put(apt, pref);
            }
        }

        /* execute gale-shapley algorithm */
        Map<Integer, Integer> rent = new TreeMap<Integer, Integer>();
        ArrayList<Integer> freeTenants = new ArrayList<Integer>();
        for (int tenant = 0; tenant < m.getTenantCount(); tenant++) {
            freeTenants.add(tenant);
        }
        while (!freeTenants.isEmpty()) {
            Integer tenant = freeTenants.remove(0);
            List<Integer> tenantPref = tenantToApartment.get(tenant);
            for (Integer apt : tenantPref) {
                if (rent.containsKey(apt)) {
                    Integer renter = rent.get(apt);
                    List<Integer> aptPref = apartmentToTenant.get(apt);
                    if (aptPref.indexOf(tenant) < aptPref.indexOf(renter)) {
                        rent.put(apt, tenant);
                        freeTenants.add(renter);
                        break;
                    }
                } else {
                    rent.put(apt, tenant);
                    break;
                }
            }
        }

        /* put everything back into the correct format */
        Map<Integer, Integer> tenantMatching = new TreeMap<Integer, Integer>();
        for (Integer apt : rent.keySet()) {
            tenantMatching.put(rent.get(apt), apt);
        }
        ArrayList<Integer> tenant_matching = new ArrayList<Integer>();
        for (int tenant = 0; tenant < m.getTenantCount(); tenant++) {
            tenant_matching.add(tenantMatching.get(tenant));
        }
        m.setTenantMatching(tenant_matching);
        return m;
    }
}
