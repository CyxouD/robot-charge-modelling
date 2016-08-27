package com.cyxoud.robots.entities;

/**
 * Represents part of a charger
 */
abstract public class ChargerPart {

    /** Name of the charger part. Useful for logging/testing */
    private String name;
    // current owner of the charger part. null if charger part hasn't have owner yet
    private Robot owner;

    public ChargerPart() {}

    /**
     *  @param name name of the charger part
     */
    public ChargerPart(String name) {
        this.name = name;
    }

    /**
     * Robot in parameter owns the charger part if it doesn't have owner previously
     * @param possibleOwner robot that wants get charger part
     * @return true if charger part was taken and false otherwise
     */
    public synchronized boolean tryBeTaken(Robot possibleOwner) {
        if (owner == null) {
            owner = possibleOwner;
            return true;
        }

        return false;
    }

    /**
     * Robot in parameter frees charger part if it had it previously, if successfully owner is set to null
     * @param possibleOwner robot that wants free charger part
     * @return true if charger part was freed and false otherwise
     */
    public synchronized boolean tryBeFreed(Robot possibleOwner) {
        if (owner == possibleOwner) {
            owner = null;
            return true;
        }

        return false;
    }

    /**
     * @return true if charger part doesn't have owner
     */
    public synchronized boolean isFree() {
        return owner == null;
    }

    public String getName() {
        return name;
    }

    /**
     * @return name of the class if name is not assigned or name otherwise
     */
    @Override
    public String toString() {
        return (name == null ? getClass().getSimpleName() : name);
    }
}
