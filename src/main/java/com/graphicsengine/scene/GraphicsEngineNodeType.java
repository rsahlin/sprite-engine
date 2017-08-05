package com.graphicsengine.scene;

import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.ui.Element;
import com.nucleus.common.Type;
import com.nucleus.component.ComponentNode;
import com.nucleus.scene.Node;

/**
 * The different type of nodes that are defined and handled by the Graphics Engine
 * 
 * @author Richard Sahlin
 *
 */
public enum GraphicsEngineNodeType implements Type<Node> {

    playfieldNode(PlayfieldNode.class),
    sharedMeshNode(SharedMeshQuad.class),
    quadNode(QuadParentNode.class),
    spriteComponentNode(ComponentNode.class),
    /**
     * UI base node
     */
    element(Element.class);

    private final Class<?> theClass;

    private GraphicsEngineNodeType(Class<?> theClass) {
        this.theClass = theClass;
    }

    /**
     * Returns the class to instantiate for the different types
     * 
     * @return
     */
    @Override
    public Class<?> getTypeClass() {
        return theClass;
    }

    @Override
    public String getName() {
        return name();
    }

}
