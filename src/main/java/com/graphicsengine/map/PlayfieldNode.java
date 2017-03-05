package com.graphicsengine.map;

import java.io.FileNotFoundException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.dataflow.ArrayInputData;
import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.io.ExternalReference;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeException;
import com.nucleus.vecmath.Axis;
import com.nucleus.vecmath.Rectangle;

/**
 * The playfield node that contains a mesh and can be put in a scene.
 * It has a map that make up the chars in the playfield (mesh) - the map in this class
 * can be larger than the map displayed by the mesh to create scrolling.
 * This is the main node for a tiled charmap (playfield) that can be rendered.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldNode extends Node {

    /**
     * Reference to map data
     */
    @SerializedName("mapRef")
    private ExternalReference mapRef;
    /**
     * The size of the map in this controller
     */
    @SerializedName("mapSize")
    private int[] mapSize = new int[2];

    /**
     * X and Y offset for map, this controls where the first char of the map is.
     */
    @SerializedName("offset")
    private float[] offset;

    /**
     * The rectangle defining the chars, all chars will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName("rect")
    private Rectangle rectangle;

    /**
     * The map data used by this controller.
     */
    transient private int[] mapData;

    /**
     * 
     */
    transient private int[] flags;
    
    public PlayfieldNode() {
    }

    @Override
    public PlayfieldNode createInstance() {
        PlayfieldNode copy = new PlayfieldNode();
        return copy;
    }

    @Override
    public PlayfieldNode copy() {
        PlayfieldNode copy = createInstance();
        copy.set(this);
        return copy;
    }

    /**
     * Copies the values from the source node to this node, this will not copy transient values.
     * 
     * @param source
     */
    protected void set(PlayfieldNode source) {
        super.set(source);
        mapRef = source.mapRef;
        setMapSize(source.mapSize);
        setCharRectangle(source.rectangle);
        setMapOffset(source.offset);

    }
    
    /**
     * Sets the map in this node, creating the map storage if needed, and updates the mesh to contain
     * the charset.
     * 
     * @param resources The scene resources
     * @throws NodeException If referenced map can not be loaded.
     */
    public void createMap(GraphicsEngineResourcesData resources) throws NodeException {
        PropertyMapper mapper = new PropertyMapper(getMeshes().get(0).getMaterial().getProgram());
        try {
            Map data = MapFactory.createMap(mapRef);
            createMap(getMapSize());
            ArrayInputData id = data.getArrayInput();
            PlayfieldMesh playfield = (PlayfieldMesh) getMeshes().get(0);
            if (data.getExternalReference() != null) {
                try {
                    Map loaded = MapFactory.createMap(data.getExternalReference());
                    playfield.copyCharmap(mapper, loaded);
                } catch (FileNotFoundException e) {
                    throw new NodeException("Could not load playfield " + data.getExternalReference().getSource());
                }
            } else if (id != null) {
                if (mapData == null) {
                    mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
                }
                id.copyArray(mapData,
                        mapSize[Axis.WIDTH.index],
                        mapSize[Axis.HEIGHT.index], 0, 0);
                playfield.copyCharmap(mapper, getMapData(), getFlags(), 0, 0, getMapData().length);
            } else {
                if (data.getMap() != null && data.getMapSize() != null) {
                    playfield.copyCharmap(mapper, data);
                }
            }
        } catch (FileNotFoundException e) {
            throw new NodeException(e);
        }

    }

    /**
     * Returns a reference to the map size, do NOT modify these values
     * 
     * @return
     */
    public int[] getMapSize() {
        return mapSize;
    }

    /**
     * Returns a reference to the rectangle defining each char
     * 
     * @return
     */
    public Rectangle getCharRectangle() {
        return rectangle;
    }

    /**
     * Returns a reference to the map data, do NOT modify these values
     * 
     * @return
     */
    public int[] getMapData() {
        return mapData;
    }

    /**
     * Returns a reference to the flag data, do NOT modify these values
     * @return
     */
    public int[] getFlags() {
        return flags;
    }
    
    /**
     * Returns the map offset if set, or null
     * The map offset controls where the first char in the map is.
     * 
     * @return
     */
    public float[] getMapOffset() {
        return offset;
    }

    /**
     * Returns the name of the map for this playfieldnode, this is used when importing
     * 
     * @return
     */
    public ExternalReference getMapRef() {
        return mapRef;
    }

    /**
     * Sets the mapsize from the source values, internal method.
     * This will not create storage for map.
     * 
     * @param mapSize
     */
    private void setMapSize(int[] mapSize) {
        System.arraycopy(mapSize, 0, this.mapSize, 0, 2);
    }

    /**
     * Sets the map offset to that of the offset, values are copied.
     * 
     * @param offset Offset values, or null to remove.
     */
    private void setMapOffset(float[] offset) {
        if (offset == null) {
            this.offset = null;
            return;
        }
        this.offset = new float[offset.length];
        System.arraycopy(offset, 0, this.offset, 0, offset.length);
    }

    /**
     * Sets the rectangle defining each char
     * 
     * @param rectangle defining each char
     */
    private void setCharRectangle(Rectangle rectangle) {
        this.rectangle = new Rectangle(rectangle);

    }

    /**
     * Creates the map storage for this node, this map may be larger than the map displayed by the mesh.
     * 
     * @param mapSize
     */
    private void createMap(int[] mapSize) {
        mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
    }
}
