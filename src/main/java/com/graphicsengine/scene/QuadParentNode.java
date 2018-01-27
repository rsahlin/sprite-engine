package com.graphicsengine.scene;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.component.SpriteComponent;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.SimpleLogger;
import com.nucleus.component.Component;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;

/**
 * Node containing Quad elements, the intended usage is to group as many quad objects as possible under one node
 * and use the mesh in the parent node to render the objects.
 * This means that they need to share the mesh in this node.
 * Use this node for simple objects that does not need to have special behavior. Use component node for that,
 * see {@linkplain Component} or {@link SpriteComponent}
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class QuadParentNode extends Node implements Consumer {

    public static final String MAX_QUADS = "maxQuads";

    @SerializedName(MAX_QUADS)
    private int maxQuads;

    transient private ArrayList<SharedMeshQuad> quadChildren = new ArrayList<>();

    /**
     * Used by GSON and {@link #createInstance(RootNode)} method - do NOT call directly
     */
    @Deprecated
    protected QuadParentNode() {
    }

    private QuadParentNode(RootNode root) {
        super(root, GraphicsEngineNodeType.quadNode);
    }

    @Override
    public Node createInstance(RootNode root) {
        QuadParentNode copy = new QuadParentNode(root);
        copy.set(this);
        return copy;
    }

    /**
     * Sets the data from the source to this, this will not copy transient values or children.
     * 
     * @param source
     */
    public void set(QuadParentNode source) {
        super.set(source);
        this.maxQuads = source.maxQuads;
    }

    /**
     * Returns the max number of quads that this node can handle
     * This is specified when loading the node.
     * 
     * @return
     */
    public int getMaxQuads() {
        return maxQuads;
    }

    public int addQuad(SharedMeshQuad quadMeshNode) {
        int index = quadChildren.size();
        quadChildren.add(quadMeshNode);
        return index;
    }

    public void removeQuad(SharedMeshQuad quadMeshNode) {
        quadChildren.remove(quadMeshNode);
    }

    @Override
    public void onCreated() {
        super.onCreated();
        // Setup all children in this node
        SpriteMesh mesh = (SpriteMesh) getMesh(MeshType.MAIN);
        for (Node n : getChildren()) {
            if (n instanceof SharedMeshQuad) {
                // This is a special case since the mesh belongs to this node.
                int index = addQuad((SharedMeshQuad) n);
                ((SharedMeshQuad) n).onCreated(mesh, index);
            }
        }
        mesh.setAttributeUpdater(this);
    }

    @Override
    public void updateAttributeData() {
        SimpleLogger.d(getClass(), "update");
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        // TODO Auto-generated method stub

    }

}
