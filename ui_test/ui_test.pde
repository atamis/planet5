
import java.util.concurrent.TimeUnit;



float max_energy=1000, cur_energy = 100;

int bar_height = 45;
int bar_width = 1024;

int menu_width = 50;
int menu_start = 1024 - menu_width;

int timer_width = 60;
int timer_start = 1024 - menu_width - timer_width;

int energy_bar_start = 6 * (bar_height-2) + 1;
int energy_bar_width = (bar_width - 6 * (bar_height - 2) - 2) - menu_width - timer_width;

void setup() {
  size(bar_width, bar_height);
  background(0);
  smooth();
}

void draw() {
  stroke(0);
  // Draw building rectangles
  for (int i = 0; i < 6; i++) {
    fill(255);
    rect(i*(bar_height-2), 1, bar_height-3, bar_height-3);
    fill(0);
    text(i + 1, i*(bar_height-2) + (bar_height-3)/2.0, bar_height/2.0);
  }
  
  // Draw the max energy bar.
  fill(211, 211, 0);
  rect(energy_bar_start, 1, energy_bar_width, bar_height-3);
  
  // Draw the current energy bar.
  fill(255, 255, 0);
  rect(energy_bar_start, 1, map(cur_energy, 0, max_energy, 0, energy_bar_width), bar_height-3);
  
  // Draw the energy text.
  textAlign(CENTER);
  fill(0);
  text("" + String.format("%.2f", cur_energy) + "/" + String.format("%.2f", max_energy), energy_bar_start + energy_bar_width/2.0, bar_height/2.0);

  // Draw the timer
  fill(0);
  stroke(0);
  rect(timer_start, 0, timer_width, bar_height);
  fill(255);
  stroke(255);
  textAlign(LEFT);
  long milli = millis();
  long minutes = TimeUnit.MILLISECONDS.toMinutes(milli);
  long seconds = TimeUnit.MILLISECONDS.toSeconds(milli) - 
    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milli));
  String time = String.format("%02d:%02d", minutes,
                                seconds);
  text(time, timer_start, bar_height/2.0);

  // Draw the menu.
  fill(0);
  stroke(0);
  rect(menu_start, 0, menu_width, bar_height);
  fill(255);
  stroke(255);
  text("Pause", menu_start, 1.0/3.0 * bar_height);
  text("Menu", menu_start, 2.0/3.0 * bar_height);





  // Move the energy bar around.
  cur_energy = map(mouseX, 0, width, 0, max_energy);
}

