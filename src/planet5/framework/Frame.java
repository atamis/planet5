package planet5.framework;

import java.util.ArrayList;

public abstract class Frame {
	private ArrayList<Button> buttons = new ArrayList<Button>();
	public Applet p;

	public Frame(Applet parent) {
		this.p = parent;
	}

	// basic methods
	public void paint() {
		draw();

		for (Button button : buttons) {
			button.paintComponent();
		}
	}

	public void update() { }
	protected abstract void draw();

	// button methods
	public void addButton(Button button) {
		buttons.add(button);
		button.p = p;
		button.listener = this;
	}

	void updateButtons() {
		for (Button button : buttons) {
			button.update();
		}
	}

	// keyboard and mouse listeners
	protected void mouseMoved() {}

	protected void mouseDragged() {}

	protected void mousePressed() {}

	protected void mouseReleased() {}

	protected void mouseClicked() {}

	protected void keyTyped() {}

	protected void keyReleased() {}

	protected void keyPressed() {}

	// button event listeners
	public void buttonPressed(String command) {}

	public void buttonClicked(String command) {}

	// mouse event listeners that process mouse input before passing the event
	// to the child class
	void processMousePressed() {
		for (Button button : buttons) {
			button.mousePressed();
		}
		mousePressed();
	}

	void processMouseReleased() {
		for (Button button : buttons) {
			button.mouseReleased();
		}
		mouseReleased();
	}

	protected void mouseWheelMoved(int unitsMovedDown) {}
}
