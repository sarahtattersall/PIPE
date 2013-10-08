package pipe.modules.queryeditor.evaluator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;

public class StatusIndicator extends JLabel
implements
	QueryConstants,
	EvaluatorGuiLoggingHandler,
	ActionListener
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3798744475408235823L;

	private URL					iconURL;

	private int					positionX;
	private int					positionY;
	private String				status;
	private final boolean		bigCircles;

	private String				statusToFlash, currentFlashingStatus;

	private final Timer			flasher;

	public StatusIndicator(final double posX, final double posY) {
		super();
		this.bigCircles = true;
		this.flasher = new Timer(1000, this);
		this.flasher.setInitialDelay(0);
		this.setStatus(QueryConstants.EVALNOTSUPPORTED);
		if (this.bigCircles)
		{
			this.setPosition(posX + QueryConstants.NODE_WIDTH + 5, posY + 8); // for
		}
		else
		{
			this.setPosition(posX + QueryConstants.NODE_WIDTH + 5, posY); // for
			// small
			// status
			// indicators
		}
	}

	public void actionPerformed(final ActionEvent event)
	{
		if (this.currentFlashingStatus.equals(this.statusToFlash))
		{
			this.currentFlashingStatus = QueryConstants.EVALNOTSUPPORTED;
		}
		else
		{
			this.currentFlashingStatus = this.statusToFlash;
		}
		this.setStatusImage(this.currentFlashingStatus);
	}

	public String getStatus()
	{
		return this.status;
	}

	void setPosition(final double x, final double y)
	{
		this.positionX = (int) x;
		this.positionY = (int) y;
		this.updateSize();
		this.updatePosition();
	}

	public void setStatus(final String newStatus)
	{
		this.status = newStatus;
		if (this.flasher.isRunning())
		{
			this.flasher.stop();
		}

		this.setStatusImage(newStatus);

		if (newStatus.equals(QueryConstants.EVALINPROGRESS) || newStatus.equals(QueryConstants.EVALFAILED))
		{
			this.currentFlashingStatus = this.statusToFlash = newStatus;
			this.flasher.start();
		}

	}

	private void setStatusImage(final String newStatus)
	{
		if (newStatus.equals(QueryConstants.EVALNOTSUPPORTED))
		{
			// evaluation not supported yet
			if (this.bigCircles)
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-nimpl.png");
			}
			else
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-nimpl-small.png");
			}
		}
		else if (newStatus.equals(QueryConstants.EVALNOTSTARTED))
		{
			// evaluation not started yet
			if (this.bigCircles)
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-nstar.png");
			}
			else
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-nstar-small.png");
			}
		}
		else if (newStatus.equals(QueryConstants.EVALINPROGRESS))
		{
			// evaluation in progress
			if (this.bigCircles)
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-iprog.png");
			}
			else
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-iprog-small.png");
			}

		}
		else if (newStatus.equals(QueryConstants.EVALCOMPLETE))
		{
			// evaluation complete
			if (this.bigCircles)
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-compl.png");
			}
			else
			{
				this.iconURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + "Eval-compl-small.png");
			}
		}
		else if (newStatus.equals(QueryConstants.EVALFAILED))
		{
			this.iconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath +
																						"Eval-failed.png");
		}
		this.setIcon(new ImageIcon(this.iconURL));
		this.repaint();
	}

	void updatePosition()
	{
		this.setLocation(this.positionX - this.getPreferredSize().width, this.positionY -
																			this.getPreferredSize().height);
	}

	void updateSize()
	{
		// To get round Java bug #4352983, the size needs to be expanded a bit
		this.setSize(	(int) (this.getPreferredSize().width * 1.2),
						(int) (this.getPreferredSize().height * 1.2));
	}
}
