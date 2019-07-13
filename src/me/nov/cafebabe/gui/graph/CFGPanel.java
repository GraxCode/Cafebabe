package me.nov.cafebabe.gui.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.objectweb.asm.tree.MethodNode;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;

import me.nov.cafebabe.analysis.blocks.Block;
import me.nov.cafebabe.analysis.blocks.Converter;
import me.nov.cafebabe.gui.graph.CFGraph.CFGComponent;
import me.nov.cafebabe.gui.graph.layout.PatchedHierarchicalLayout;
import me.nov.cafebabe.gui.graph.vertex.BlockVertex;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.ui.Images;

public class CFGPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private MethodNode mn;
	private ArrayList<Block> blocks = new ArrayList<>();
	private CFGraph graph;
	private CFGComponent graphComponent;
	private JScrollPane scrollPane;

	public CFGPanel(MethodNode mn) {
		this.mn = mn;
		this.setLayout(new BorderLayout(0, 0));
		this.graph = new CFGraph();
		JPanel lpad = new JPanel();
		lpad.setBorder(new EmptyBorder(1, 5, 0, 1));
		lpad.setLayout(new GridLayout());
		lpad.add(new JLabel("Control Flow Graph"));
		JPanel rs = new JPanel();
		rs.setLayout(new GridLayout(1, 5));
		for (int i = 0; i < 3; i++)
			rs.add(new JPanel());
		JButton save = new JButton("Save");
		save.addActionListener(l -> {
			File parentDir = new File(System.getProperty("user.home") + File.separator + "Desktop");
			JFileChooser jfc = new JFileChooser(parentDir);
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setFileFilter(new FileNameExtensionFilter("Bitmap image file (.bmp)", "bmp"));
			jfc.addChoosableFileFilter(new FileNameExtensionFilter("Portable Network Graphics (.png)", "png"));
			if (mn.name.length() < 32) {
				jfc.setSelectedFile(new File(parentDir, mn.name + ".bmp"));
			} else {
				jfc.setSelectedFile(new File(parentDir, "method.bmp"));
			}
			int result = jfc.showSaveDialog(CFGPanel.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File output = jfc.getSelectedFile();
				String type = ((FileNameExtensionFilter) jfc.getFileFilter()).getExtensions()[0];
				BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
				try {
					ImageIO.write(Images.watermark(image), type, output);
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}
			}
		});
		rs.add(save);
		JButton reload = new JButton("Reload");
		reload.addActionListener(l -> {
			generateGraph();
		});
		rs.add(reload);
		lpad.add(rs);
		this.add(lpad, BorderLayout.NORTH);

		graphComponent = graph.getComponent();
		graphComponent.scp = scrollPane;
		JPanel inner = new JPanel();
		inner.setBorder(new EmptyBorder(30, 30, 30, 30));
		inner.setLayout(new BorderLayout(0, 0));
		inner.setBackground(Color.WHITE);
		inner.add(graphComponent, BorderLayout.CENTER);
		scrollPane = new JScrollPane(inner);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.add(scrollPane, BorderLayout.CENTER);
		this.generateGraph();
	}

	public void generateGraph() {
		blocks.clear();
		if (mn.instructions.size() == 0) {
			this.clear();
			return;
		}
		graphComponent.scp = scrollPane;
		Converter c = new Converter(mn);
		try {
			blocks.addAll(c.convert(true, true, true, 2));
		} catch (Exception e) {
			e.printStackTrace();
			this.clear();
			return;
		}
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
			existing.clear();
			if (!blocks.isEmpty()) {
				boolean first = true;
				for (Block b : blocks) {
					if (b.getInput().isEmpty() || first) {
						addBlock((mxCell) parent, b, null);
						first = false;
					}
				}
			}
			graph.getView().setScale(1);
			PatchedHierarchicalLayout layout = new PatchedHierarchicalLayout(graph);
			layout.setFineTuning(true);
			layout.setIntraCellSpacing(25d);
			layout.setInterRankCellSpacing(80d);
			layout.setDisableEdgeStyle(true);
			layout.setParallelEdgeSpacing(100d);
			layout.setUseBoundingBox(true);
			layout.execute(graph.getDefaultParent());
			// mxCompactTreeLayout layout = new mxCompactTreeLayout(graph);
			// layout.setResetEdges(true);
			// layout.setEdgeRouting(true);
			// layout.setHorizontal(false);
			// layout.setMoveTree(true);
			// layout.setUseBoundingBox(true);
			// layout.execute(graph.getDefaultParent());

			/*
			 * Alternative layout that is prettier but can't handle complicated flow: mxCompactTreeLayout layout = new mxCompactTreeLayout(graph); layout.setResetEdges(true); layout.setEdgeRouting(true); layout.setHorizontal(false); layout.setMoveTree(true); layout.setUseBoundingBox(true); layout.execute(graph.getDefaultParent());
			 **/
		} finally {
			graph.getModel().endUpdate();
		}
		this.revalidate();
		this.repaint();
		// TODO set horizontal scroll to half
	}

	private HashMap<Block, mxCell> existing = new HashMap<>();

	private mxCell addBlock(mxCell parent, Block b, BlockVertex input) {
		mxCell v1 = null;
		if (existing.containsKey(b)) {
			mxCell cached = existing.get(b);
			if (input != null) {
				((BlockVertex) cached.getValue()).addInput(input);
			}
			return cached;
		}
		BlockVertex vertex = new BlockVertex(mn, b, b.getNodes(), b.getLabel(),
				mn.instructions.indexOf(b.getNodes().get(0)));
		if (input != null) {
			vertex.addInput(input);
		}
		v1 = (mxCell) graph.insertVertex(parent, null, vertex, 150, 10, 80, 40,
				"fillColor=#FFFFFF;fontColor=#111111;strokeColor=#9297a1");
		graph.updateCellSize(v1); // resize cell

		existing.put(b, v1);
		if (v1 == null) {
			throw new RuntimeException();
		}
		ArrayList<Block> next = b.getOutput();
		for (int i = 0; i < next.size(); i++) {
			Block out = next.get(i);
			if (out.equals(b)) {
				graph.insertEdge(parent, null, null, v1, v1, "strokeColor=" + getEdgeColor(b, i) + ";");
			} else {
				assert (out.getInput().contains(b));
				mxCell vertexOut = addBlock(parent, out, vertex);
				graph.insertEdge(parent, null, null, v1, vertexOut, "strokeColor=" + getEdgeColor(b, i) + ";");
			}
		}
		return v1;
	}

	private String getEdgeColor(Block b, int i) {
		if (b.endsWithJump()) {
			if (b.getOutput().size() > 1) {
				if (i == 0) {
					return Colors.jumpColorGreen;
				}
				return Colors.jumpColorRed;
			}
			return Colors.jumpColor;
		}
		if (b.endsWithSwitch()) {
			if (i == 0) {
				return Colors.jumpColorPink;
			}
			return Colors.jumpColorPurple;
		}
		return Colors.edgeColor;
	}

	public void clear() {
		graph.getModel().beginUpdate();
		graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
		graph.getModel().endUpdate();
	}
}
