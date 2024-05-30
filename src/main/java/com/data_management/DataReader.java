package com.data_management;

/**
 * This file represents an interface for data readers in health monitoring simulations.
 * Implementing classes define specific strategies for reading patient data.
 */
public interface DataReader {

    /**
     * Connects to a WebSocket server to read data continuously and store it in the data storage.
     *
     * @param dataStorage the storage where data will be stored
     * @param serverUri the URI of the WebSocket server
     * @throws Exception if there is an error connecting to the server
     */
    void readData(DataStorage dataStorage, String serverUri) throws Exception;

}
