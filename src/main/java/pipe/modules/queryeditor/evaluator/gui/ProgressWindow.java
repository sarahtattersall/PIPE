/**
 * ProgressWindow
 * 
 * This is the popup that shows the progress of the evaluation of a 
 * Performance Tree query.
 * 
 * @author Tamas Suto
 * @date 24/11/07
 */

package pipe.modules.queryeditor.evaluator.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import pipe.modules.interfaces.Cleanable;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.QueryEvaluator;

public class ProgressWindow extends JDialog implements EvaluatorGuiLoggingHandler, MouseListener, Cleanable
{

	public class TimedTextUpdater
	{
		private final Queue<String>		texts;
		private final Timer				timer;
		private boolean					allowChangeProgressBar;
		private final Lock				textLock	= new ReentrantLock();

		private final ActionListener	updater		= new ActionListener()
													{

														public void actionPerformed(final ActionEvent event)
														{
															TimedTextUpdater.this.textLock.lock();
															try
															{
																if (TimedTextUpdater.this.allowChangeProgressBar)
																{
																	String finishedTail = ". Click Operation Nodes to view results";
																	String msg = TimedTextUpdater.this.texts.remove();
																	if (msg.equals(QueryConstants.failedComplete))
																	{
																		ProgressWindow.this.progressBar.setForeground(Color.RED);
																		msg += finishedTail;
																		TimedTextUpdater.this.stopChangingProgressBarText();
																		ProgressWindow.this.fillProgressBar();
																	}
																	else if (msg.equals(QueryConstants.timeoutComplete))
																	{
																		ProgressWindow.this.progressBar.setForeground(Color.RED);
																		msg += finishedTail;
																		TimedTextUpdater.this.stopChangingProgressBarText();
																		ProgressWindow.this.fillProgressBar();
																	}
																	else if (msg.equals(QueryConstants.successfulComplete))
																	{
																		ProgressWindow.this.progressBar.setForeground(Color.GREEN);
																		msg += finishedTail;
																	}
																	else if (msg.startsWith(QueryConstants.successfulResultStringStart))
																	{
																		ProgressWindow.this.progressBar.setForeground(Color.GREEN);
																		TimedTextUpdater.this.stopChangingProgressBarText();
																		ProgressWindow.this.fillProgressBar();
																	}
																	ProgressWindow.this.progressBar.setString(msg.trim());
																}
															}
															finally
															{
																if (TimedTextUpdater.this.texts.isEmpty())
																{
																	TimedTextUpdater.this.timer.stop();
																}
																TimedTextUpdater.this.textLock.unlock();
															}
														}
													};

		public TimedTextUpdater() {
			this.timer = new Timer(500, this.updater);
			this.allowChangeProgressBar = true;
			this.texts = new LinkedList<String>();
		}

		public void start(final String text)
		{
			this.textLock.lock();
			try
			{
				this.texts.add(text);
				if (this.timer.isRunning())
				{

				}
				else
				{
					this.timer.start();
				}
			}
			finally
			{
				this.textLock.unlock();
			}
		}

		private void stopChangingProgressBarText()
		{
			TimedTextUpdater.this.allowChangeProgressBar = false;
		}
	}
	private class WindowHandler extends WindowAdapter
	{
		// Handler for window closing event
		@Override
		public void windowClosing(final WindowEvent e)
		{
			// clear data when closing, in case we launch the module again
			ProgressWindow.this.cleanUp();
		}
	}

	private static final long					serialVersionUID		= 1L;

	private final String						cancel					= "Cancel";
    private ProgressView						progressViewer;

	private JTabbedPane							viewer;
	private final ImageIcon									closeIcon;
    private JProgressBar						progressBar;
	private JButton								inputButton;

	private Dimension							viewPanelPreferredSize, tabPanelPreferredSize;
	private int									incrementPortion, incrementAmount, noProgBarPortions;
	private final Timer							incrementer, paintWhileScrolling;
	private final TimedTextUpdater				textUpdater;
	private volatile int						incrementTo;

	private final Lock							progressLock			= new ReentrantLock();
	private final HashMap<Integer, Component>	currentlyDisplayedTabs	= new HashMap<Integer, Component>();

	public final Hashtable<Integer, Component>	allComponents			= new Hashtable<Integer, Component>();

	private final Hashtable<Integer, Component>	tabComponentMap			= new Hashtable<Integer, Component>();

	private final ActionListener								buttonListener			= new ActionListener()
																		{
																			public void actionPerformed(ActionEvent event)
																			{
																				if (event.getSource() == ProgressWindow.this.inputButton)
																				{
																					ProgressWindow.this.cleanUp();
																				}
																			}
																		};

    private int									vertScrollHash, horizScrollHash;

	public ProgressWindow() {
		super(QueryManager.getEditor(), "Performance Query Evaluation Progress Tracker", false);

		// indicate that we have started query evaluation;
		QueryManager.setProgressWindow(this);
		QueryManager.setEvaluatingQuery(true);

		final URL iconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath +
																						"Close.png");
		this.closeIcon = new ImageIcon(iconURL);
        ActionListener timedIncrementer = new ActionListener()
        {
            public void actionPerformed(final ActionEvent e)
            {
                ProgressWindow.this.progressLock.lock();
                try
                {
                    int currentFill = ProgressWindow.this.progressBar.getValue();
                    currentFill += ProgressWindow.this.incrementAmount;
                    ProgressWindow.this.progressBar.setValue(currentFill);
                    ProgressWindow.this.incrementTo -= ProgressWindow.this.incrementAmount;
                    if(ProgressWindow.this.incrementTo <= 0)
                    {
                        ProgressWindow.this.incrementTo = 0;
                        ProgressWindow.this.progressBar.setIndeterminate(true);
                        ProgressWindow.this.incrementer.stop();
                    }
                    else
                    {
                        ProgressWindow.this.progressBar.setIndeterminate(false);
                    }

                    final int max = ProgressWindow.this.progressBar.getMaximum();
                    if(currentFill >= max)
                    {
                        ProgressWindow.this.incrementer.stop();
                    }
                    else if(currentFill +
                            ProgressWindow.this.incrementTo >= max)
                    {
                        ProgressWindow.this.progressBar.setIndeterminate(false);
                    }
                }
                finally
                {
                    ProgressWindow.this.progressLock.unlock();
                }
            }
        };
        this.incrementer = new Timer(100, timedIncrementer);
		this.incrementer.setInitialDelay(0);

        ActionListener scrollingTimer = new ActionListener()
        {
            public void actionPerformed(final ActionEvent e)
            {
                ProgressWindow.this.progressViewer.ensurePainted();
            }
        };
        this.paintWhileScrolling = new Timer(100, scrollingTimer);

		this.textUpdater = new TimedTextUpdater();

		this.initDialogue();
	}

	public synchronized void addTab(final Component c, final String name)
	{
		if (!this.currentlyDisplayedTabs.containsKey(c.hashCode()))
		{
			c.setSize(this.tabPanelPreferredSize);

			final JScrollPane tabPanel = new JScrollPane(c);
			tabPanel.setPreferredSize(this.viewPanelPreferredSize);

			this.viewer.addTab(name, new CloseTabIcon(this.closeIcon), tabPanel);
			this.currentlyDisplayedTabs.put(c.hashCode(), tabPanel);
			this.tabComponentMap.put(tabPanel.hashCode(), c);
		}
		this.viewer.setSelectedComponent(this.currentlyDisplayedTabs.get(c.hashCode()));
	}

	public void cleanUp()
	{
		ProgressWindow.this.stopProcessing();
		ProgressWindow.this.closeWindow();
	}

	private void closeWindow()
	{
		// indicate that we have finished query evaluation;
		QueryManager.getEditor().setEnabled(true);
		QueryManager.setProgressWindow(null);
		QueryManager.setEvaluatingQuery(false);
		this.progressViewer = null;
		this.setVisible(false);
		this.dispose();
	}

	public void fillProgressBar()
	{
		this.progressLock.lock();
		try
		{
			this.incrementer.setDelay(50);
			final int currentFill = this.progressBar.getValue();
			if (this.incrementer.isRunning())
			{
				this.incrementTo += this.noProgBarPortions - currentFill;
			}
			else
			{
				this.incrementTo = this.noProgBarPortions - currentFill;
				if (this.incrementTo > 0)
				{
					this.incrementer.start();
				}
			}
		}
		finally
		{
			this.progressLock.unlock();
		}
	}

	public void finish()
	{
	}

	public ProgressView getProgressView()
	{
		return this.progressViewer;
	}

	/**
	 * @return the tabPanelPreferredSize
	 */
	public Dimension getTabPanelPreferredSize()
	{
		return this.tabPanelPreferredSize;
	}

	public void incrementProgressBar(final String status)
	{
		this.progressLock.lock();
		try
		{
			double additional = this.incrementPortion;
			if (status.equals(QueryConstants.EVALCOMPLETE))
			{
				additional *= 2.0 / 3.0;
			}
			else
			{
				additional *= 4.0 / 3.0;
			}

			this.incrementTo += additional;

			if (!this.incrementer.isRunning())
			{
				this.incrementer.start();
			}
		}
		finally
		{
			this.progressLock.unlock();
		}
	}

	private void initDialogue()
	{
		// Tabbed Pane
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int viewPanelPreferredWidth = screenSize.width * 72 / 100;
		final int viewPanelPreferredHeight = screenSize.height * 72 / 100;
		this.viewPanelPreferredSize = new Dimension(viewPanelPreferredWidth, viewPanelPreferredHeight);

		this.viewer = new JTabbedPane(SwingConstants.TOP);
		this.viewer.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.viewer.addMouseListener(this);
		this.viewer.setBorder(new EtchedBorder());
		this.viewer.setPreferredSize(this.viewPanelPreferredSize);

		final int tabPanelPreferredHeight = viewPanelPreferredHeight * 90 / 100;

		this.tabPanelPreferredSize = new Dimension(viewPanelPreferredWidth, tabPanelPreferredHeight);

		// progress view
		this.progressViewer = new ProgressView();
		this.progressViewer.setParent(this);
		this.progressViewer.drawQueryTree(this.tabPanelPreferredSize);
        JScrollPane scrollPane = new JScrollPane(this.progressViewer);
		// this.scrollPane.setPreferredSize(this.viewPanelPreferredSize);
		scrollPane.setPreferredSize(this.progressViewer.getSize());

		this.vertScrollHash = scrollPane.getVerticalScrollBar().hashCode();
		this.horizScrollHash = scrollPane.getHorizontalScrollBar().hashCode();

		scrollPane.getHorizontalScrollBar().addMouseListener(this);
		scrollPane.getVerticalScrollBar().addMouseListener(this);

		// add progress view to tabbedpane
		this.viewer.add(scrollPane, this.progressViewer.getName());

		// get no of opNodes and multiply this by 2
		// this gives us the number of states each op node has to go through to
		// reach completion
		// hence we can fill the bar 1 increment amount when each op node is
		// updated.
		this.noProgBarPortions = this.progressViewer.getNumOpNodes() * 200;
		this.incrementPortion = 100;
		this.incrementAmount = this.incrementPortion / 10;
		this.incrementTo = 0;

		this.progressBar = new JProgressBar(0, this.noProgBarPortions);
		this.progressBar.setStringPainted(true);
		this.progressBar.setString("Starting...");
		this.progressBar.setBorderPainted(true);
		this.progressBar.setPreferredSize(new Dimension(viewPanelPreferredWidth / 2,
														this.progressBar.getHeight()));

		// Cancel/OK button & buttonpanel
		this.inputButton = new JButton(this.cancel);
		this.inputButton.addActionListener(this.buttonListener);

		final Container buttonContainer = Box.createHorizontalBox();

		buttonContainer.add(Box.createGlue());
		buttonContainer.add(this.progressBar);
		buttonContainer.add(Box.createHorizontalStrut(5));
		buttonContainer.add(this.inputButton);
		buttonContainer.add(Box.createGlue());

		// Put everything together
        JSplitPane progressWindowPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.viewer, buttonContainer);

		this.add(progressWindowPanel, BorderLayout.CENTER);
		this.addWindowListener(new WindowHandler());
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	boolean isVertOrHorizScrollBar(final MouseEvent e)
	{
		final int hash = e.getSource().hashCode();
		return hash == this.vertScrollHash || hash == this.horizScrollHash;
	}

	public void mouseClicked(final MouseEvent e)
	{
		if (e.getSource().equals(this.viewer))
		{
			final int tabNumber = this.viewer.getUI().tabForCoordinate(this.viewer, e.getX(), e.getY());
			if (tabNumber >= 0)
			{
				final Icon icon = this.viewer.getIconAt(tabNumber);
				if (icon != null)
				{
					final Rectangle rect = ((CloseTabIcon) icon).getBounds();
					if (rect.contains(e.getX(), e.getY()))
					{
						final Component tab = this.viewer.getComponentAt(tabNumber);
						this.currentlyDisplayedTabs.remove(this.tabComponentMap	.remove(tab.hashCode())
																				.hashCode());
						this.viewer.removeTabAt(tabNumber);
					}
				}
			}
		}
	}

	public void mouseEntered(final MouseEvent e)
	{
	}

	public void mouseExited(final MouseEvent e)
	{
	}

	public void mousePressed(final MouseEvent e)
	{
		if (this.isVertOrHorizScrollBar(e))
		{
			this.paintWhileScrolling.start();
		}
	}

	public void mouseReleased(final MouseEvent e)
	{
		if (this.paintWhileScrolling.isRunning())
		{
			this.paintWhileScrolling.stop();
		}
	}

	public synchronized void setProgressBarText(final String stuff)
	{
		this.textUpdater.start(stuff);
	}

	public void showDialogue()
	{
		this.setVisible(true);
	}

	private void stopProcessing()
	{
		QueryEvaluator.stopAnalysis();
	}

	public void swapButton()
	{
		final String buttonText = this.inputButton.getText();
        String ok = "  OK  ";
        if (buttonText.equals(this.cancel))
		{
			this.inputButton.setText(ok);
		}
		else if (buttonText.equals(ok))
		{
			this.inputButton.setText(this.cancel);
		}
	}

}
