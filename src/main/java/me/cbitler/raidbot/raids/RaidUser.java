package me.cbitler.raidbot.raids;

/**
 * Represents a raid user
 * This class is not commented as the method names should be self-explanatory
 * @author Christopher Bitler
 */
public class RaidUser {
    String id;
    String name;
    String spec;
    String role;

    public RaidUser(String id, String name, String spec, String role) {
        this.id = id;
        this.name = name;
        this.spec = spec;
        this.role = role;
    }
    
    /**
     * the equals method
     * @param other the object to compare to
     * @return true if all members are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
    	if (other instanceof RaidUser) {
    		RaidUser otherRole = (RaidUser) other;
    		if (this.id.equals(otherRole.id)
    			&& this.name.equals(otherRole.name)
    			&& this.spec.equals(otherRole.spec)
    			&& this.role.equals(otherRole.role)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * the hashCode method
     * @return true if all members are equal, false otherwise
     */
    @Override
    public int hashCode() {
    	return this.id.hashCode()
    			+ this.name.hashCode()
    			+ this.role.hashCode()
    			+ this.spec.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
