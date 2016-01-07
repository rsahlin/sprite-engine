package com.graphicsengine.tiledsprite;

import com.google.gson.annotations.SerializedName;
import com.nucleus.data.Anchor;
import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Axis;

/**
 * A number of sprites that will be rendered using the same Mesh, ie all sprites in this class are rendered using one
 * draw call.
 * This class only contains the drawable parts of the sprites - no logic is contained in this class.
 * TODO: Create a TiledMesh class that is used by both this class and PlayfieldMesh
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteMesh extends Mesh implements AttributeUpdater {

    @SerializedName("count")
    int count;
    /**
     * Width and height of each sprite.
     */
    @SerializedName("size")
    private float[] size = new float[2];
    /**
     * Anchor value for each sprites, 0 to 1 where 0 is upper/left and 1 is lower/right assuming vertices
     * are
     */
    @SerializedName("anchor")
    private Anchor anchor;
    /**
     * Reference to tiled texture
     */
    @SerializedName("textureref")
    private String textureRef;

    /**
     * Contains attribute data for all sprites.
     * This data must be mapped into the mesh for changes to take place.
     */
    transient float[] attributeData;

    /**
     * Creates a new sprite sheet using one mesh, the mesh must be created before being used.
     * 
     * @param spriteCount
     */
    public TiledSpriteMesh(int spriteCount) {
        setup(spriteCount);
    }

    /**
     * Creates a new instance of the tiled sprite mesh based on the source.
     * This will NOT create the mesh and sprites it will only set the values from the source.
     * {@link #createMesh(TiledSpriteProgram, Texture2D, float[], float[])}
     * 
     * @param source
     */
    public TiledSpriteMesh(TiledSpriteMesh source) {
        super();
        set(source);
    }

    /**
     * Sets the values from the source mesh, this will only set copy values it will not create the mesh.
     * 
     * @param source
     */
    public void set(TiledSpriteMesh source) {
        setId(source.getId());
        this.textureRef = source.textureRef;
        if (source.anchor != null) {
            anchor = new Anchor(source.anchor);
        }
        setSize(source.getSize());
        setup(source.getCount());

    }

    private void setup(int spriteCount) {
        count = spriteCount;
        attributeData = new float[count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE];
    }

    /**
     * Internal method, sets the size of each char.
     * This will only set the size parameter, createMesh must be called to actually create the mesh
     * 
     * @param size The size to set, or null to not set any values.
     */
    private void setSize(float[] size) {
        if (size != null) {
            this.size[Axis.WIDTH.index] = size[Axis.WIDTH.index];
            this.size[Axis.HEIGHT.index] = size[Axis.HEIGHT.index];
        }
    }

    /**
     * Creates the Mesh to be rendered, after this call the all the sprites in this controller can be rendered
     * by fetching the mesh and rendering it.
     * Note that this class will be set as AttributeUpdater in the mesh in order for the sprites to be displayed
     * properly.
     * 
     * @param program
     * @param texture The texture to use for sprites, must be {@link TiledTexture2D} otherwise tiling will not work.
     * @return
     */
    public void createMesh(TiledSpriteProgram program, Texture2D texture) {
        program.buildMesh(this, texture, count, size, anchor);

        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);

    }

    /**
     * Returns the number of sprites for this tiled spritesheet, this is the max number of sprites (quads) that
     * can be displayed.
     * 
     * @return Number of sprites in the spritesheet.
     */
    public int getCount() {
        return count;
    }

    @Override
    public void setAttributeData() {
        // TODO Index the numbers for vertice/attribute data instead of using 0/1
        VertexBuffer positions = getVerticeBuffer(BufferIndex.ATTRIBUTES);
        positions.setArray(getAttributeData(), 0, 0, count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
    }

    @Override
    public float[] getAttributeData() {
        return attributeData;
    }

    @Override
    public void destroy() {
        attributeData = null;
    }

    /**
     * Returns the dimension of the sprites, in x and y
     * 
     * @return Width and height of sprite, at index 0 and 1 respectively.
     */
    public float[] getSize() {
        return size;
    }

    /**
     * Returns the tiled texture at the specified active texture index, for tiled sheets that only have 1 texture index
     * will always be 0
     * 
     * @param index Index to texture, starts at 0 and increases.
     * @return The tiled texture
     * @throws ArrayIndexOutOfBoundsException If index < 0 or > number of textures - 1
     */
    public TiledTexture2D getTiledTexture(int index) {
        return (TiledTexture2D) getTexture(index);
    }

    /**
     * Returns the texture reference or null if not set
     * 
     * @return
     */
    public String getTextureRef() {
        return textureRef;
    }

}
