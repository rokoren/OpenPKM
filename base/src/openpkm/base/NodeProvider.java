/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import org.openide.nodes.Node;

/**
 *
 * @author Rok Koren
 */
public interface NodeProvider
{
    Node findNode(Node root, Object target);
}
