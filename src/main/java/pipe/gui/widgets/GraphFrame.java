package pipe.gui.widgets;

import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import net.sourceforge.jpowergraph.layout.Layouter;
import net.sourceforge.jpowergraph.layout.spring.SpringLayoutStrategy;
import net.sourceforge.jpowergraph.lens.*;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.popup.PopupManipulator;
import net.sourceforge.jpowergraph.painters.edge.LineEdgePainter;
import net.sourceforge.jpowergraph.painters.edge.LoopEdgePainter;
import net.sourceforge.jpowergraph.swing.SwingJGraphPane;
import net.sourceforge.jpowergraph.swing.SwingJGraphScrollPane;
import net.sourceforge.jpowergraph.swing.SwingJGraphViewPane;
import net.sourceforge.jpowergraph.swing.manipulator.SwingPopupDisplayer;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import pipe.extensions.jpowergraph.*;
import pipe.gui.ApplicationSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class GraphFrame extends JFrame
{

    public void constructGraphFrame(DefaultGraph graph, String markingLegend)
    {
        this.setIconImage(new ImageIcon(ApplicationSettings.getImagePath() + "icon.png").getImage());

        setSize(800, 600);
        setLocation(100, 100);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent wev)
            {
                Window w = wev.getWindow();
                w.setVisible(false);
                w.dispose();
            }
        });

        SwingJGraphPane jGraphPane = new SwingJGraphPane(graph);

        LensSet lensSet = new LensSet();
        lensSet.addLens(new RotateLens());
        lensSet.addLens(new TranslateLens());
        lensSet.addLens(new ZoomLens());

        CursorLens m_draggingLens = new CursorLens();
        lensSet.addLens(m_draggingLens);

        lensSet.addLens(new TooltipLens());
        lensSet.addLens(new LegendLens());
        lensSet.addLens(new NodeSizeLens());

        jGraphPane.setLens(lensSet);


        jGraphPane.addManipulator(new DraggingManipulator(m_draggingLens, -1));
        jGraphPane.addManipulator(new PopupManipulator(jGraphPane,
                                                       (TooltipLens) lensSet.getFirstLensOfType(TooltipLens.class)));

        jGraphPane.setNodePainter(PIPETangibleState.class,
                                  PIPETangibleState.getShapeNodePainter());

        jGraphPane.setNodePainter(PIPEInitialTangibleState.class,
                                  PIPEInitialTangibleState.getShapeNodePainter());

        jGraphPane.setNodePainter(PIPEVanishingState.class,
                                  PIPEVanishingState.getShapeNodePainter());

        jGraphPane.setNodePainter(PIPEInitialVanishingState.class,
                                  PIPEInitialVanishingState.getShapeNodePainter());

        jGraphPane.setNodePainter(PIPEVanishingState.class,
                                  PIPEVanishingState.getShapeNodePainter());

        jGraphPane.setNodePainter(PIPEState.class,
                                  PIPEState.getShapeNodePainter());

        jGraphPane.setNodePainter(PIPEInitialState.class,
                                  PIPEInitialState.getShapeNodePainter());

        jGraphPane.setDefaultNodePainter(PIPENode.getShapeNodePainter());

        jGraphPane.setEdgePainter(TextEdge.class,
                                  new PIPELineWithTextEdgePainter(JPowerGraphColor.BLACK,
                                                                  JPowerGraphColor.GRAY, false));

        jGraphPane.setDefaultEdgePainter(new LineEdgePainter(JPowerGraphColor.BLACK,
                                                             JPowerGraphColor.GRAY, false));

        jGraphPane.setEdgePainter(PIPELoopWithTextEdge.class,
                                  new PIPELoopWithTextEdgePainter(JPowerGraphColor.GRAY,
                                                                  JPowerGraphColor.GRAY, LoopEdgePainter.RECTANGULAR));

        jGraphPane.setAntialias(true);

        jGraphPane.setPopupDisplayer(new SwingPopupDisplayer(new PIPESwingToolTipListener(),
                                                             new PIPESwingContextMenuListener(graph, new LensSet(), new Integer[]{}, new Integer[]{})));

        Layouter m_layouter = new Layouter(new SpringLayoutStrategy(graph));
        m_layouter.start();

        SwingJGraphScrollPane scroll =
                new SwingJGraphScrollPane(jGraphPane, lensSet);

        SwingJGraphViewPane view = new SwingJGraphViewPane(scroll, lensSet, true);


        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JPanel panel = new JPanel();
        panel.setLayout(gbl);
        getContentPane().add("Center", view);
        getContentPane().add("West", panel);
        if(!markingLegend.equals("") && markingLegend != null)
        {
            markingLegend = "Marking corresponds to " + markingLegend;
        }

        JTextArea legend = new JTextArea(markingLegend +
                                                 "\nHover mouse over nodes to view state marking");
        legend.setEditable(false);

        getContentPane().add("South", legend);

        setVisible(true);
    }
}
