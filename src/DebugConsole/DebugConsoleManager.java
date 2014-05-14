package DebugConsole;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.layers.Layer;
import iScope.Props;
import iScope.WWJApplet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import openLS.OpenLSManager;
import wms.PickableWMSLayer;
import Kml.KmlPilot;
import javax.swing.JToggleButton;

public class DebugConsoleManager
{
	public WWJApplet applet;
	JPanel debugPanel;
	OutputStream defaultError = System.err;
	OutputStream defaultOut = System.out;
	JTextField lat1;
	JTextField lon1;
	JTextField lat2;
	JTextField lon2;
	LayersJTree tree;
	JComboBox<String> pilotSolarLayers = new JComboBox<String>();
	JComboBox<String> pilotNoiseLayer = new JComboBox<String>();
	JComboBox<String> pilotLayersBox = new JComboBox<String>();
	public static JTextPane textPaneWMSready;

	public DebugConsoleManager(WWJApplet applet)
	{
		this.applet = applet;
		WWJApplet.getWWD().addKeyListener(debugKeyListener);
		init();
		debugPanel.setVisible(false);
	}

	Layer lastlayer = null;

	// DO NOT REMOVE THIS:
	/**
	 * @wbp.parser.entryPoint
	 */
	private void init()
	{
		debugPanel = new JPanel(new BorderLayout());
		debugPanel.setPreferredSize(new Dimension(320, 800));
		applet.getContentPane().add(debugPanel, BorderLayout.EAST);
		JPanel panel_2 = new JPanel();
		debugPanel.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		JPanel panel_1 = new JPanel();
		debugPanel.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if (sourceTabbedPane.getTitleAt(index).equalsIgnoreCase("layers"))
				{
					tree.updateList();
				}
			}
		});
		panel_1.add(tabbedPane);
		final JPanel pilotsPanel = new JPanel();
		pilotsPanel.setLayout(null);
		tabbedPane.addTab("Pilots", null, pilotsPanel, null);
		JLabel label_3 = new JLabel("Load Pilot");
		label_3.setBounds(12, 12, 95, 14);
		pilotsPanel.add(label_3);
		JComboBox<String> pilotsComboBox = new JComboBox<String>();
		final List<String> pilotsNames = Props.getStrList("pilots");
		pilotsComboBox.addItem("");
		for (String pilot : pilotsNames)
		{
			pilotsComboBox.addItem(pilot);
		}
		pilotsComboBox.addActionListener(new ActionListener()
		{
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				String pilot = (String) cb.getSelectedItem();
				try
				{
					if (pilot != "" && pilot != null)
						WWJApplet.getPilotManager().loadPilot(pilot.toUpperCase());
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		pilotsComboBox.setBounds(12, 28, 293, 22);
		pilotsPanel.add(pilotsComboBox);
		// pilotNoiseLayer = new JComboBox<String>();
		pilotNoiseLayer.setBounds(12, 148, 293, 22);
		pilotNoiseLayer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				String layer = (String) cb.getSelectedItem();
				if (layer != "" && layer != null)
				{
					WWJApplet.jS.avarageNoiseMapDisplay(layer);
					// if (lastlayer != null)
					// WWJApplet.getWWD().getModel().getLayers().remove(lastlayer);
					// lastlayer = WWJApplet.getPilotManager().getCurrentPilot()
					// .getNoiseLayers().getLayerByName(layer);
					// WWJApplet.getWWD().getModel().getLayers().add(lastlayer);
					// lastlayer.setEnabled(true);
				}
			}
		});
		pilotsPanel.add(pilotNoiseLayer);
		// pilotSolarLayers= new JComboBox<String>();
		pilotSolarLayers.setBounds(12, 181, 293, 22);
		pilotSolarLayers.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				String layer = (String) cb.getSelectedItem();
				if (layer != "" && layer != null)
				{
					if (lastlayer != null)
						WWJApplet.getWWD().getModel().getLayers().remove(lastlayer);
					lastlayer = WWJApplet.getPilotManager().getCurrentPilot().getPilotLayerList().getLayerByName(layer);
					WWJApplet.getWWD().getModel().getLayers().add(lastlayer);
					lastlayer.setEnabled(true);
				}
			}
		});
		pilotsPanel.add(pilotSolarLayers);
		pilotLayersBox.setToolTipText("Not ready");
		pilotLayersBox.setBounds(12, 109, 293, 22);
		pilotLayersBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				String layer = (String) cb.getSelectedItem();
				if (layer != "" && layer != null)
				{
					if (lastlayer != null)
						WWJApplet.getWWD().getModel().getLayers().remove(lastlayer);
					lastlayer = WWJApplet.getPilotManager().getCurrentPilot().getPilotLayerList().getLayerByName(layer);
					WWJApplet.getWWD().getModel().getLayers().add(lastlayer);
					lastlayer.setEnabled(true);
				}
			}
		});
		pilotsPanel.add(pilotLayersBox);
		JLabel lblLoadedPilotLayers = new JLabel("Loaded Pilot Layers");
		lblLoadedPilotLayers.setBounds(12, 96, 95, 14);
		pilotsPanel.add(lblLoadedPilotLayers);
		JLabel lblLoadedPilotNoise = new JLabel("Loaded Pilot NOISE Layers");
		lblLoadedPilotNoise.setBounds(12, 133, 182, 14);
		pilotsPanel.add(lblLoadedPilotNoise);
		JLabel lblLoadedPilotSolar = new JLabel("Loaded Pilot SOLAR Layers");
		lblLoadedPilotSolar.setBounds(12, 169, 182, 14);
		pilotsPanel.add(lblLoadedPilotSolar);
		JButton btnTurnOffLast = new JButton("Turn OFF last layer");
		btnTurnOffLast.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (lastlayer!=null) lastlayer.setEnabled(false);
			}
		});
		btnTurnOffLast.setBounds(76, 214, 149, 23);
		pilotsPanel.add(btnTurnOffLast);
		final JCheckBox chckbxShowPilotTiles = new JCheckBox("show Pilot tiles");
		chckbxShowPilotTiles.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (chckbxShowPilotTiles.isSelected() && WWJApplet.getPilotManager().getCurrentPilot() != null)
				{
					WWJApplet.getJS().showCurrentPilotTiles(true);
				} else
				{
					WWJApplet.getJS().showCurrentPilotTiles(false);
				}
			}
		});
		chckbxShowPilotTiles.setBounds(12, 277, 97, 23);
		pilotsPanel.add(chckbxShowPilotTiles);
		JButton btnRefreshLayersList = new JButton("Refresh layers list");
		btnRefreshLayersList.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				dinamicPilotLayersMenu();
			}
		});
		btnRefreshLayersList.setBounds(38, 61, 149, 23);
		pilotsPanel.add(btnRefreshLayersList);
		JToggleButton tglbtnRetriveSolarWfs = new JToggleButton("retrive solar wfs");
		tglbtnRetriveSolarWfs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.jS.retriveSolarInfos(!WWJApplet.retriveWfsSolarInfos);
			}
		});
		tglbtnRetriveSolarWfs.setBounds(12, 323, 121, 23);
		pilotsPanel.add(tglbtnRetriveSolarWfs);
		JToggleButton tglbtnRetriveBldInfos = new JToggleButton("retrive bld infos wfs");
		tglbtnRetriveBldInfos.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.jS.retriveBuildingInfos(!WWJApplet.retriveWfsBldInfos);
			}
		});
		tglbtnRetriveBldInfos.setBounds(143, 323, 135, 23);
		pilotsPanel.add(tglbtnRetriveBldInfos);
		textPaneWMSready = new JTextPane();
		textPaneWMSready.setText("Not ready");
		textPaneWMSready.setToolTipText("");
		textPaneWMSready.setBounds(197, 64, 108, 20);
		pilotsPanel.add(textPaneWMSready);
		JButton btnUnloadd = new JButton("Unload 3D");
		btnUnloadd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.getPilotManager().currentPilot.unload3DLayer();
			}
		});
		btnUnloadd.setBounds(189, 248, 89, 23);
		pilotsPanel.add(btnUnloadd);
		JButton btnLoadd = new JButton("Load3D");
		btnLoadd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.getPilotManager().currentPilot.load3DLayer();
			}
		});
		btnLoadd.setBounds(189, 277, 89, 23);
		pilotsPanel.add(btnLoadd);
		JPanel routingPanel = new JPanel();
		tabbedPane.addTab("Routing", null, routingPanel, null);
		routingPanel.setLayout(null);
		JComboBox<String> openLSServerComboBox = new JComboBox<>();
		openLSServerComboBox.addActionListener(new ActionListener()
		{
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				applet.openLSManager.openLSServer = (String) cb.getSelectedItem();
			}
		});
		openLSServerComboBox.setEditable(true);
		openLSServerComboBox.setBounds(10, 27, 295, 22);
		routingPanel.add(openLSServerComboBox);
		openLSServerComboBox.addItem(Props.getStr("openLSServer"));
		int i = 1;
		while (Props.getStr("openLSServer" + i) != null)
		{
			openLSServerComboBox.addItem(Props.getStr("openLSServer" + i));
			i++;
		}
		JLabel lblOpenlsServer = new JLabel("OpenLS Server");
		lblOpenlsServer.setBounds(10, 11, 95, 14);
		routingPanel.add(lblOpenlsServer);
		JButton btnDoRequest = new JButton("Do Request");
		btnDoRequest.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// List<OpenLSPosition> pos = new ArrayList<>();
				// pos.add(OpenLSPosition.fromDegrees(Double.parseDouble(lat1.getText()), Double.parseDouble(lon1.getText()), 0));
				// pos.add(OpenLSPosition.fromDegrees(Double.parseDouble(lat2.getText()), Double.parseDouble(lon2.getText()), 0));
				WWJApplet.getJS().doRoutingPickRequest("Fastest", null, null, null);
			}
		});
		btnDoRequest.setBounds(196, 248, 109, 23);
		routingPanel.add(btnDoRequest);
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Coordinates", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.setBounds(10, 109, 295, 128);
		routingPanel.add(panel_4);
		panel_4.setLayout(null);
		lat1 = new JTextField();
		lat1.setColumns(10);
		lat1.setBounds(10, 66, 127, 20);
		panel_4.add(lat1);
		lon1 = new JTextField();
		lon1.setColumns(10);
		lon1.setBounds(158, 66, 127, 20);
		panel_4.add(lon1);
		lat2 = new JTextField();
		lat2.setColumns(10);
		lat2.setBounds(10, 97, 127, 20);
		panel_4.add(lat2);
		lon2 = new JTextField();
		lon2.setColumns(10);
		lon2.setBounds(158, 97, 127, 20);
		panel_4.add(lon2);
		JLabel label_1 = new JLabel("Latitude");
		label_1.setBounds(49, 41, 49, 14);
		panel_4.add(label_1);
		JLabel label_2 = new JLabel("Longitude");
		label_2.setBounds(189, 41, 67, 14);
		panel_4.add(label_2);
		JComboBox<String> requestTypeComboBox = new JComboBox<>();
		requestTypeComboBox.addActionListener(new ActionListener()
		{
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				OpenLSManager.getDefaultPreferences().routePreference = (String) cb.getSelectedItem();
			}
		});
		requestTypeComboBox.setEditable(true);
		requestTypeComboBox.setBounds(10, 76, 295, 22);
		routingPanel.add(requestTypeComboBox);
		requestTypeComboBox.addItem("Fastest");
		requestTypeComboBox.addItem("Shortest");
		requestTypeComboBox.addItem("Pedestrian");
		requestTypeComboBox.addItem("Wheelchair");
		requestTypeComboBox.addItem("VisuallyImpaired");
		JLabel label = new JLabel("Request Type");
		label.setBounds(10, 60, 95, 14);
		routingPanel.add(label);
		JButton btnStartPicking = new JButton("Pick!");
		btnStartPicking.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.routingPickingManager.startPicking();
			}
		});
		btnStartPicking.setActionCommand("Start Picking");
		btnStartPicking.setBounds(10, 248, 109, 23);
		routingPanel.add(btnStartPicking);
		JPanel testAndOld = new JPanel();
		tabbedPane.addTab("test&Old", null, testAndOld, null);
		testAndOld.setLayout(null);
		JLabel lblPilot = new JLabel("Load KML Pilot");
		lblPilot.setBounds(12, 12, 95, 14);
		testAndOld.add(lblPilot);
		JComboBox<String> kmlPilotComboBox = new JComboBox<String>();
		if (Props.getBool("KMLEnabled"))
		{
			final List<KmlPilot> kmlPilots = WWJApplet.getApplet().kmlManager.getKmlPilots();
			for (KmlPilot kmlPilot : kmlPilots)
			{
				kmlPilotComboBox.addItem(kmlPilot.getName());
			}
			kmlPilotComboBox.addItem("NONE");
			kmlPilotComboBox.setBounds(12, 28, 295, 22);
			kmlPilotComboBox.addActionListener(new ActionListener()
			{
				@SuppressWarnings("unchecked")
				public void actionPerformed(ActionEvent e)
				{
					JComboBox<String> cb = (JComboBox<String>) e.getSource();
					String pilot = (String) cb.getSelectedItem();
					if (pilot.equals("NONE"))
					{
						WWJApplet.getApplet().kmlManager.unloatAllPilots();
					} else
					{
						WWJApplet.getApplet().kmlManager.loadPilot(pilot);
					}
				}
			});
			testAndOld.add(kmlPilotComboBox);
			JSlider slider = new JSlider();
			slider.setValue(0);
			slider.setMaximum(200);
			slider.setMinimum(-200);
			slider.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					JSlider source = (JSlider) e.getSource();
					for (KmlPilot kmlPilot : kmlPilots)
					{
						if (kmlPilot.isLoaded())
						{
							kmlPilot.getController().getKmlRoot().getRoot().setDetailHint(((int) source.getValue()) / 100f);
							WWJApplet.getWWD().redrawNow();
						}
					}
				}
			});
			slider.setBounds(12, 82, 291, 16);
			testAndOld.add(slider);
			JLabel lblKmlDetailHint = new JLabel("KML Detail Hint");
			lblKmlDetailHint.setBounds(12, 61, 142, 16);
			testAndOld.add(lblKmlDetailHint);
		}
		JButton btnNewButton_2 = new JButton("Load Custom KML Pilot");
		btnNewButton_2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// LOAD CUSTOM PILOT
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					System.out.println("Opening: " + file.getPath());
					WWJApplet.getApplet().kmlManager.loadExternalPilot(file.getPath());
				} else
				{
					System.out.println("Open command cancelled by user.");
				}
			}
		});
		btnNewButton_2.setBounds(10, 26, 208, 23);
		testAndOld.add(btnNewButton_2);
		JLabel lblAddAPlacemark = new JLabel("Add a placemark");
		lblAddAPlacemark.setBounds(12, 86, 95, 14);
		testAndOld.add(lblAddAPlacemark);
		JButton btnPlaceSemirandomPlacemark = new JButton("Place semiRandom placeMark");
		btnPlaceSemirandomPlacemark.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WWJApplet.jS.addPlacemark(46 + Math.random(), 11 + Math.random());
			}
		});
		btnPlaceSemirandomPlacemark.setBounds(10, 100, 208, 23);
		testAndOld.add(btnPlaceSemirandomPlacemark);
		JPanel noisePanel = new JPanel();
		tabbedPane.addTab("Noise", null, noisePanel, null);
		JButton btnRequestNoisePoints = new JButton("Request Noise Points");
		btnRequestNoisePoints.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println(WWJApplet.jS.sendSecureNoiseRequest(null, null, null, null, null, null, null));
				WWJApplet.GUI.noiseSelector.disable();
			}
		});
		JButton btnNewButton = new JButton("New Sector Selector");
		btnNewButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (WWJApplet.GUI.noiseSelector.isEnable())
				{
					WWJApplet.getApplet();
					WWJApplet.GUI.noiseSelector.disable();
				}
				WWJApplet.GUI.noiseSelector.enable();
			}
		});
		JButton btnIncreaseMarkerDimenion = new JButton("Increase marker dimenion");
		btnIncreaseMarkerDimenion.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.getApplet().noiseManager.changeMarkerDimension(5);
			}
		});
		JButton btnDecreaseMarkerDimenion = new JButton("Decrease marker dimenion");
		btnDecreaseMarkerDimenion.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.getApplet().noiseManager.changeMarkerDimension(-5);
			}
		});
		JButton btnGenerateAggregateMap = new JButton("Generate Aggregate Map");
		btnGenerateAggregateMap.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				String startDate = startTimeTextField.getText(); // "2013-01-01T08:15:00"; // YYYY-MM-DDThh:mm:ss
				String endDate = endTimeTextField.getText();// "2013-12-31T23:59:59"; // YYYY-MM-DDThh:mm:ss
				String mapName = txtEgCles.getText(); // "CLES";
				System.out.println(WWJApplet.jS.generateAggregateNoiseMaps(startDate, endDate, "00:00", "23:59", mapName));
				WWJApplet.GUI.noiseSelector.disable();
			}
		});
		endTimeTextField = new JTextField();
		endTimeTextField.setText("2013-12-31T23:59:59");
		endTimeTextField.setColumns(10);
		txtEgCles = new JTextField();
		txtEgCles.setText("eg: Cles");
		txtEgCles.setColumns(10);
		JLabel lblStartTime = new JLabel("Start Time");
		JLabel lblEndTime = new JLabel("End Time");
		JLabel lblMapName = new JLabel("Map Name");
		startTimeTextField = new JTextField();
		startTimeTextField.setText("2013-01-01T08:15:00");
		startTimeTextField.setColumns(10);
		JButton btnClearNoisePoints = new JButton("Clear Noise Points");
		btnClearNoisePoints.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WWJApplet.jS.clearNoisePoints();
			}
		});
		GroupLayout gl_noisePanel = new GroupLayout(noisePanel);
		gl_noisePanel
				.setHorizontalGroup(gl_noisePanel
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								gl_noisePanel
										.createSequentialGroup()
										.addGroup(
												gl_noisePanel
														.createParallelGroup(Alignment.LEADING)
														.addGroup(gl_noisePanel.createSequentialGroup().addContainerGap().addComponent(btnDecreaseMarkerDimenion, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
														.addGroup(gl_noisePanel.createSequentialGroup().addContainerGap().addComponent(btnIncreaseMarkerDimenion, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
														.addGroup(gl_noisePanel.createSequentialGroup().addGap(77).addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
														.addGroup(gl_noisePanel.createSequentialGroup().addContainerGap(124, Short.MAX_VALUE).addComponent(btnGenerateAggregateMap, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE))
														.addGroup(
																gl_noisePanel
																		.createSequentialGroup()
																		.addContainerGap(102, Short.MAX_VALUE)
																		.addGroup(
																				gl_noisePanel
																						.createParallelGroup(Alignment.TRAILING)
																						.addGroup(
																								gl_noisePanel.createSequentialGroup().addComponent(lblMapName, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED)
																										.addComponent(txtEgCles, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								gl_noisePanel
																										.createSequentialGroup()
																										.addGroup(
																												gl_noisePanel.createParallelGroup(Alignment.LEADING).addComponent(lblEndTime, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE).addComponent(lblStartTime))
																										.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
																										.addGroup(
																												gl_noisePanel.createParallelGroup(Alignment.LEADING, false).addComponent(startTimeTextField)
																														.addComponent(endTimeTextField, GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)))))
														.addGroup(gl_noisePanel.createSequentialGroup().addContainerGap().addComponent(btnRequestNoisePoints).addGap(18).addComponent(btnClearNoisePoints, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		gl_noisePanel.setVerticalGroup(gl_noisePanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_noisePanel.createSequentialGroup().addContainerGap().addComponent(btnNewButton).addGap(34).addGroup(gl_noisePanel.createParallelGroup(Alignment.BASELINE).addComponent(btnRequestNoisePoints).addComponent(btnClearNoisePoints))
						.addGap(29).addGroup(gl_noisePanel.createParallelGroup(Alignment.BASELINE).addComponent(lblStartTime).addComponent(startTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_noisePanel.createParallelGroup(Alignment.BASELINE).addComponent(endTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblEndTime))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_noisePanel.createParallelGroup(Alignment.BASELINE).addComponent(txtEgCles, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblMapName))
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnGenerateAggregateMap).addGap(119).addComponent(btnIncreaseMarkerDimenion).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnDecreaseMarkerDimenion).addGap(20)));
		noisePanel.setLayout(gl_noisePanel);
		JPanel layersPanel = new JPanel();
		tabbedPane.addTab("Layers", null, layersPanel, null);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JButton btnNewButton_1 = new JButton("ON");
		btnNewButton_1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Layer layer = (Layer) LayersJTree.lastSelectedNode.getUserObject();
				layer.setEnabled(true);
			}
		});
		JButton btnOff = new JButton("OFF");
		btnOff.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Layer layer = (Layer) LayersJTree.lastSelectedNode.getUserObject();
				layer.setEnabled(false);
			}
		});
		GroupLayout gl_layersPanel = new GroupLayout(layersPanel);
		gl_layersPanel.setHorizontalGroup(gl_layersPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_layersPanel
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_layersPanel.createParallelGroup(Alignment.LEADING).addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE).addComponent(btnOff, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		gl_layersPanel.setVerticalGroup(gl_layersPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_layersPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_layersPanel.createParallelGroup(Alignment.LEADING).addGroup(gl_layersPanel.createSequentialGroup().addComponent(btnNewButton_1).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnOff).addGap(232))
										.addGroup(gl_layersPanel.createSequentialGroup().addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE).addGap(1)))));
		tree = new LayersJTree();
		scrollPane.setViewportView(tree);
		layersPanel.setLayout(gl_layersPanel);
		JPanel generalPanel = new JPanel();
		tabbedPane.addTab("Settings", null, generalPanel, null);
		JLabel lblExxageration = new JLabel("Exxageration");
		JSlider slider_1 = new JSlider();
		slider_1.setPaintTicks(true);
		slider_1.setPaintLabels(true);
		slider_1.setMinorTickSpacing(20);
		slider_1.setMaximum(1000);
		slider_1.setMajorTickSpacing(100);
		slider_1.setValue(100);
		slider_1.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				double exx = ((double) source.getValue()) / 100.0;
				WWJApplet.getWWD().getSceneController().setVerticalExaggeration(exx);
			}
		});
		JButton btndOn = new JButton("3d ON");
		btndOn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WWJApplet.jS.toggle3D(true);
			}
		});
		JButton btndOff = new JButton("3d OFF");
		btndOff.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.jS.toggle3D(false);
			}
		});
		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
			}
		});
		slider.setMinimum(-100);
		slider.setValue(100);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(20);
		slider.setMajorTickSpacing(100);
		JLabel lblYOffsetAnotation = new JLabel("Y offset anotation");
		final JSlider slider_2 = new JSlider();
		slider_2.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				WWJApplet.getWWD().getView().setFieldOfView(Angle.fromDegrees(slider_2.getValue()));
			}
		});
		slider_2.setMaximum(170);
		slider_2.setValue(45);
		slider_2.setPaintTicks(true);
		slider_2.setPaintLabels(true);
		slider_2.setMinorTickSpacing(20);
		slider_2.setMajorTickSpacing(100);
		JButton btnUndockdWindow = new JButton("Undock 3D Window");
		btnUndockdWindow.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (WWJApplet.getPilotManager().getCurrentPilot() != null)
					WWJApplet.getPilotManager().getCurrentPilot().unload3DLayer();
				WWJApplet.getApplet().setFullScreen();
				if (WWJApplet.getPilotManager().getCurrentPilot() != null)
					WWJApplet.getPilotManager().getCurrentPilot().load3DLayer();
			}
		});
		JButton btnAnotherButton = new JButton("Another Button");
		JLabel lblSetFieldOf = new JLabel("Set Field of View");
		GroupLayout gl_generalPanel = new GroupLayout(generalPanel);
		gl_generalPanel.setHorizontalGroup(gl_generalPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_generalPanel
						.createSequentialGroup()
						.addGroup(
								gl_generalPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_generalPanel
														.createSequentialGroup()
														.addContainerGap()
														.addGroup(
																gl_generalPanel.createParallelGroup(Alignment.LEADING).addComponent(slider_1, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
																		.addComponent(lblExxageration, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_generalPanel.createSequentialGroup().addGap(52).addComponent(btndOn).addGap(18).addComponent(btndOff))
										.addGroup(
												gl_generalPanel
														.createSequentialGroup()
														.addContainerGap()
														.addGroup(
																gl_generalPanel.createParallelGroup(Alignment.LEADING).addComponent(lblYOffsetAnotation, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
																		.addComponent(slider, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(btnUndockdWindow).addGap(39).addComponent(btnAnotherButton))
										.addGroup(gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(slider_2, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(lblSetFieldOf, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		gl_generalPanel.setVerticalGroup(gl_generalPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(lblExxageration).addPreferredGap(ComponentPlacement.RELATED).addComponent(slider_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(42).addGroup(gl_generalPanel.createParallelGroup(Alignment.BASELINE).addComponent(btndOn).addComponent(btndOff)).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblYOffsetAnotation).addGap(6)
						.addComponent(slider, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblSetFieldOf).addGap(13)
						.addComponent(slider_2, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE).addGap(27).addGroup(gl_generalPanel.createParallelGroup(Alignment.BASELINE).addComponent(btnUndockdWindow).addComponent(btnAnotherButton))
						.addContainerGap(411, Short.MAX_VALUE)));
		gl_generalPanel.setHorizontalGroup(gl_generalPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_generalPanel
						.createSequentialGroup()
						.addGroup(
								gl_generalPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_generalPanel
														.createSequentialGroup()
														.addContainerGap()
														.addGroup(
																gl_generalPanel.createParallelGroup(Alignment.LEADING).addComponent(slider_1, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
																		.addComponent(lblExxageration, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_generalPanel.createSequentialGroup().addGap(52).addComponent(btndOn).addGap(18).addComponent(btndOff))
										.addGroup(
												gl_generalPanel
														.createSequentialGroup()
														.addContainerGap()
														.addGroup(
																gl_generalPanel.createParallelGroup(Alignment.LEADING).addComponent(lblYOffsetAnotation, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
																		.addComponent(slider, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(btnUndockdWindow).addGap(39).addComponent(btnAnotherButton))
										.addGroup(gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(slider_2, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(lblSetFieldOf, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		gl_generalPanel.setVerticalGroup(gl_generalPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_generalPanel.createSequentialGroup().addContainerGap().addComponent(lblExxageration).addPreferredGap(ComponentPlacement.RELATED).addComponent(slider_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(42).addGroup(gl_generalPanel.createParallelGroup(Alignment.BASELINE).addComponent(btndOn).addComponent(btndOff)).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblYOffsetAnotation).addGap(6)
						.addComponent(slider, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblSetFieldOf).addGap(13)
						.addComponent(slider_2, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE).addGap(27).addGroup(gl_generalPanel.createParallelGroup(Alignment.BASELINE).addComponent(btnUndockdWindow).addComponent(btnAnotherButton))
						.addContainerGap(411, Short.MAX_VALUE)));
		generalPanel.setLayout(gl_generalPanel);
		JPanel scenarioPanel = new JPanel();
		tabbedPane.addTab("Scenario selection", null, scenarioPanel, null);
		JButton btnNoise = new JButton("Noise");
		btnNoise.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.jS.switchScenario(2);
			}
		});
		scenarioPanel.add(btnNoise);
		JButton btnSolar = new JButton("Solar");
		btnSolar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				WWJApplet.jS.switchScenario(1);
			}
		});
		scenarioPanel.add(btnSolar);
		JButton btnRouting = new JButton("Routing");
		btnRouting.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				WWJApplet.jS.switchScenario(3);
			}
		});
		scenarioPanel.add(btnRouting);
	}

	PickableWMSLayer pickableWMSLayer = null;
	KeyListener debugKeyListener = new KeyListener()
	{
		public void keyPressed(KeyEvent e)
		{
			if (e.isControlDown() && e.getKeyChar() != 'd' && e.getKeyCode() == 68)
			{
				debugPanel.setVisible(!debugPanel.isVisible());
			}
			if (e.getKeyCode() == KeyEvent.VK_F6)
			{
				try
				{
					System.out.println("f6 pressed,  method.. will start immediately");
					// WWJApplet.jS.confirmNewExport("TestAccount", "45", "NamingSchema","012345", "max");
					WWJApplet.jS.avarageNoiseMapDisplay("");
					System.out.println("f6 Done");
				} catch (Throwable e1)
				{
					System.err.println("error ");
					e1.printStackTrace();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_F7)
			{
				try
				{
					System.out.println("f7 pressed,  method.. will start immediately");
					// WWJApplet.jS.avarageNoiseMapDisplay("noise_Povo20120101_20131231");
					WWJApplet.jS.confirmNewExport("mio", "01", "naming", "0123", "max", "cgml", true);
					// WWJApplet.jS.irradiationMapDisplay("CLES_solar_04");
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		public void keyReleased(KeyEvent e)
		{
		}

		public void keyTyped(KeyEvent e)
		{
		}
	};
	private JTextField endTimeTextField;
	private JTextField txtEgCles;
	private JTextField startTimeTextField;

	public JTextField getStartTimeTextField()
	{
		return startTimeTextField;
	}

	public JTextField getEndTimeTextField()
	{
		return endTimeTextField;
	}

	public JTextField getTxtNoiseMapName()
	{
		return txtEgCles;
	}

	public void dinamicPilotLayersMenu()
	{
		try
		{
			if (pilotLayersBox.getItemCount() > 0)
				pilotLayersBox.removeAllItems();
			pilotLayersBox.addItem("");
			if (WWJApplet.getPilotManager().getCurrentPilot().getPilotLayerList().size() > 0)
			{
				for (Layer layer : WWJApplet.getPilotManager().getCurrentPilot().getPilotLayerList())
				{
					pilotLayersBox.addItem(layer.getName());
				}
			}
			if (pilotSolarLayers.getItemCount() > 0)
				pilotSolarLayers.removeAllItems();
			pilotSolarLayers.addItem("");
			if (WWJApplet.getPilotManager().getCurrentPilot().getSolarLayers().size() > 0)
			{
				for (Layer layer : WWJApplet.getPilotManager().getCurrentPilot().getSolarLayers())
				{
					pilotSolarLayers.addItem(layer.getName());
				}
			}
			if (pilotNoiseLayer.getItemCount() > 0)
				pilotNoiseLayer.removeAllItems();
			pilotNoiseLayer.addItem("");
			if (WWJApplet.getPilotManager().getCurrentPilot().getNoiseLayers().size() > 0)
			{
				for (Layer layer : WWJApplet.getPilotManager().getCurrentPilot().getNoiseLayers())
				{
					pilotNoiseLayer.addItem(layer.getName());
				}
			}
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
