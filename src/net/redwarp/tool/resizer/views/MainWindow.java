/*
 * Copryright (C) 2012 Redwarp
 * 
 * This file is part of 9Patch Resizer.
 * 9Patch Resizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * 9Patch Resizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with 9Patch Resizer.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.redwarp.tool.resizer.views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.iharder.dnd.FileDrop;
import net.redwarp.tool.resizer.ImageScaler;
import net.redwarp.tool.resizer.ScreenDensity;
import net.redwarp.tool.resizer.misc.Localization;
import net.redwarp.tool.resizer.table.Operation;
import net.redwarp.tool.resizer.table.ResultTable;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private JPanel inputPanel;
	private JPanel outputPanel;

	private ImageIcon blueArrow, redArrow;
	private ImageIcon blueArrowSmall, redArrowSmall;
	private JButton xhdpiButton;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private ResultTable resultTable;
	private JLabel instructionLabel;
	private JMenuBar menuBar;
	private JMenu mnHelp;
	private JMenu mnEdit;
	private JMenuItem mntmClear;
	private JMenuItem mntmAbout;
	private final Action action = new SwingAction();

	public MainWindow() {
		this.setSize(new Dimension(450, 350));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(Localization.get("app_name"));

		List<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/img/icon_512.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/img/icon_256.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/img/icon_128.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/img/icon_64.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/img/icon_32.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/img/icon_16.png")));
		this.setIconImages(icons);

		this.blueArrow = new ImageIcon(
				MainWindow.class.getResource("/img/blue_big.png"));
		this.redArrow = new ImageIcon(
				MainWindow.class.getResource("/img/red_big.png"));
		this.blueArrowSmall = new ImageIcon(
				MainWindow.class.getResource("/img/blue_small.png"));
		this.redArrowSmall = new ImageIcon(
				MainWindow.class.getResource("/img/red_small.png"));
		this.getContentPane().setLayout(new CardLayout(0, 0));

		this.inputPanel = new JPanel();
		this.inputPanel.setPreferredSize(new Dimension(10, 140));
		this.getContentPane().add(this.inputPanel, "input");

		this.xhdpiButton = new JButton(Localization.get("xhdpi"));
		this.xhdpiButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		this.inputPanel.setLayout(new BorderLayout(0, 0));
		this.xhdpiButton.setBorderPainted(false);
		this.xhdpiButton.setFocusPainted(false);
		this.xhdpiButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.xhdpiButton.setHorizontalTextPosition(SwingConstants.CENTER);
		this.xhdpiButton.setIcon(this.blueArrow);
		this.xhdpiButton.setSelectedIcon(this.redArrow);
		// this.xhdpiButton.setPressedIcon(this.redArrow);
		this.xhdpiButton.setBorder(null);
		this.xhdpiButton.setContentAreaFilled(false);
		this.inputPanel.add(this.xhdpiButton);

		this.outputPanel = new JPanel();
		this.getContentPane().add(this.outputPanel, "output");
		this.outputPanel.setLayout(new BorderLayout(0, 0));

		this.textArea = new JTextArea();
		this.textArea.setLineWrap(true);
		this.textArea.setEditable(false);

		this.resultTable = new ResultTable();
		this.scrollPane = new JScrollPane(this.resultTable);
		this.scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.outputPanel.add(this.scrollPane, BorderLayout.CENTER);

		FileDrop.Listener<Container> dropListener = new FileDrop.Listener<Container>() {

			@Override
			public void filesDropped(Container source, File[] files) {
				for (File input : files) {
					if (input.getName().endsWith(".png")) {
						MainWindow.this.mntmClear.setEnabled(true);
						CardLayout layout = (CardLayout) MainWindow.this
								.getContentPane().getLayout();
						layout.show(MainWindow.this.getContentPane(), "output");
						int density = ScreenDensity.XHDPI;
						Operation operation = new Operation(input);
						MainWindow.this.resultTable.addOperation(operation);

						ImageScaler scaler = new ImageScaler(operation,
								ScreenDensity.getDensity(density)) {
							@Override
							protected void process(
									java.util.List<Operation> chunks) {
								for (Operation operation : chunks) {
									MainWindow.this.resultTable
											.notifyChange(operation);
								}
							};
						};
						scaler.post();
					}
				}
			}

			@Override
			public void dragEnter(Container source) {
				MainWindow.this.xhdpiButton.setSelected(true);
				MainWindow.this.instructionLabel
						.setIcon(MainWindow.this.redArrowSmall);
			}

			@Override
			public void dragExit(Container source) {
				MainWindow.this.xhdpiButton.setSelected(false);
				MainWindow.this.instructionLabel
						.setIcon(MainWindow.this.blueArrowSmall);
			}
		};
		new FileDrop<Container>(this.getContentPane(), null, dropListener);
		new FileDrop<Container>(this.outputPanel, null, dropListener);

		this.instructionLabel = new JLabel(Localization.get("xhdpi"));
		this.instructionLabel.setIcon(this.blueArrowSmall);
		this.instructionLabel.setBorder(new EmptyBorder(4, 4, 4, 4));
		this.outputPanel.add(this.instructionLabel, BorderLayout.SOUTH);

		new FileDrop<Container>(this.textArea, null, dropListener);

		this.setMenuBar();
	}

	private void setMenuBar() {
		this.menuBar = new JMenuBar();
		this.setJMenuBar(this.menuBar);

		this.mnEdit = new JMenu(Localization.get("menu_edit"));
		this.menuBar.add(this.mnEdit);

		this.mntmClear = new JMenuItem(Localization.get("menu_item_clear"));
		this.mntmClear.setAction(this.action);
		this.mntmClear.setEnabled(false);
		this.mnEdit.add(this.mntmClear);

		this.mnHelp = new JMenu(Localization.get("menu_help"));
		this.menuBar.add(this.mnHelp);

		this.mntmAbout = new JMenuItem();
		this.mntmAbout.setAction(new AboutAction());
		this.mnHelp.add(this.mntmAbout);
	}

	private class AboutAction extends AbstractAction {
		public AboutAction() {
			this.putValue(NAME, Localization.get("menu_item_about"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new AboutDialog(MainWindow.this).setVisible(true);
		}
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			this.putValue(NAME, Localization.get("menu_item_clear"));
			this.putValue(SHORT_DESCRIPTION,
					Localization.get("menu_item_clear_desc"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			MainWindow.this.resultTable.clear();
			if (MainWindow.this.resultTable.getModel().getRowCount() == 0) {
				MainWindow.this.mntmClear.setEnabled(false);

				CardLayout layout = (CardLayout) MainWindow.this
						.getContentPane().getLayout();
				layout.show(MainWindow.this.getContentPane(), "input");
			}
		}
	}
}