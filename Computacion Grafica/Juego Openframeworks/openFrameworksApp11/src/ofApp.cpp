#include "ofApp.h"
#include <math.h>


ofSoundPlayer  tetris;
ofSoundPlayer  incorrecto;
ofSoundPlayer  correcto;
float triangle1[6] = { 0, 0, 0, 250, 125, 125 };
float triangle2[6] = { 0, 0, 250, 0, 125, 125 };
float triangle3[6] = { 114, 250, 250, 250, 250,125 };     //correctos
float triangle4[6] = { 0, 250, 114, 250, 60,190 };

float triangle1C[6] = { 0, 0, 0, 250, 125, 125 };
float triangle2C[6] = { 0, 0, 250, 0, 125, 125 };
float triangle3C[6] = { 114, 250, 250, 250, 250,125 };
float triangle4C[6] = { 0, 250, 114, 250, 60,190 };


float triangle1b[6];
float triangle2b[6];
float triangle3b[6];
float triangle4b[6];

float scaling = 0.5;
float NuevoPx1;
float NuevoPx2;
float NuevoPx3;

int completes;
int canvas[4] = {0,0,250,250};
int shapeSelect = 1;
float angC = cos(5 * 3.14159 / 180);
float angT = sin(5 * 2 * 3.14159 / 360);
//--------------------------------------------------------------
void ofApp::setup(){
	tetris.load("tetris.mp3");
	correcto.load("correcto.mp3");
	incorrecto.load("incorrecto.mp3");
	tetris.setLoop(true);
	tetris.play();
}

//--------------------------------------------------------------
void ofApp::update(){
	
}

void triangle(float array[6]) {

	ofDrawTriangle(array[0], array[1], array[2], array[3], array[4], array[5]);
}

void scuare(int array[4]) {
	
	ofDrawRectangle(array[0], array[1], array[2], array[3]);
}



void ofApp::ifVertex() {

}



//--------------------------------------------------------------
void ofApp::draw(){
	
	ofSetColor(255, 0, 0);
	triangle(triangle1);
	triangle(triangle1b);
	ofSetColor(0, 255, 0);
	triangle(triangle2);
	triangle(triangle2b);
	ofSetColor(0, 0, 255);
	triangle(triangle3);
	triangle(triangle3b);
	ofSetColor(0, 0, 0);
	triangle(triangle4);
	triangle(triangle4b);

}

//--------------------------------------------------------------
void ofApp::keyPressed(int key){
	
	
	switch (key)
	{
		case ofKey::OF_KEY_F1:
		{
			if (shapeSelect > 1) {
				shapeSelect -= 1;
			}
			
		break;
	
		}
		case ofKey::OF_KEY_F2:
		{
			if (shapeSelect < 4) {
				shapeSelect += 1;
			}
			
		break;
		}

		case ofKey::OF_KEY_CONTROL:
		{
			
			triangle1b[0] = 0;
			triangle1b[1] = 0;
			triangle1b[2] = 0;
			triangle1b[3] = 250;
			triangle1b[4] = 125;
			triangle1b[5] = 125;

			triangle2b[0] = 0;
			triangle2b[1] = 0;
			triangle2b[2] = 250;
			triangle2b[3] = 0;
			triangle2b[4] = 125;
			triangle2b[5] = 125;

			triangle3b[0] = 114;
			triangle3b[1] = 250;
			triangle3b[2] = 250;
			triangle3b[3] = 250;
			triangle3b[4] = 250;
			triangle3b[5] = 125;
			
			triangle4b[0] = 0;
			triangle4b[1] = 250;
			triangle4b[2] = 114;
			triangle4b[3] = 250;
			triangle4b[4] = 60;
			triangle4b[5] = 190;
			break;
		}
		case ofKey::OF_KEY_DOWN:
		{	
			if (shapeSelect == 1) {

				triangle1[1] += 5;
				triangle1[3] += 5;
				triangle1[5] += 5;
			}
			else if (shapeSelect == 2) {
				triangle2[1] += 5;
				triangle2[3] += 5;
				triangle2[5] += 5;
			}
			else if (shapeSelect == 3) {
				triangle3[1] += 5;
				triangle3[3] += 5;
				triangle3[5] += 5;
			}
			else if (shapeSelect == 4) {
				triangle4[1] += 5;
				triangle4[3] += 5;
				triangle4[5] += 5;
			}
	
		break;
		}
		
		case ofKey::OF_KEY_UP:
		{
			if (shapeSelect == 1) {
				triangle1[1] -= 5;
				triangle1[3] -= 5;
				triangle1[5] -= 5;
			}
			else if (shapeSelect == 2) {
				triangle2[1] -= 5;
				triangle2[3] -= 5;
				triangle2[5] -= 5;
			}
			else if (shapeSelect == 3) {
				triangle3[1] -= 5;
				triangle3[3] -= 5;
				triangle3[5] -= 5;
			}
			else if (shapeSelect == 4) {
				triangle4[1] -= 5;
				triangle4[3] -= 5;
				triangle4[5] -= 5;
			}

			break;
		}
		case ofKey::OF_KEY_LEFT:
		{
			if (shapeSelect == 1) {
				triangle1[0] -= 5;
				triangle1[2] -= 5;
				triangle1[4] -= 5;
			}
			else if (shapeSelect == 2) {
				triangle2[0] -= 5;
				triangle2[2] -= 5;
				triangle2[4] -= 5;
			}
			else if (shapeSelect == 3) {
				triangle3[0] -= 5;
				triangle3[2] -= 5;
				triangle3[4] -= 5;
			}
			else if (shapeSelect == 4) {
				triangle4[0] -= 5;
				triangle4[2] -= 5;
				triangle4[4] -= 5;
			}

			break;
		}
		case ofKey::OF_KEY_RIGHT:
		{
			if (shapeSelect == 1) {

				triangle1[0] += 5;
				triangle1[2] += 5;
				triangle1[4] += 5;

				
			}
			else if (shapeSelect == 2) {
				triangle2[0] += 5;
				triangle2[2] += 5;
				triangle2[4] += 5;

			}
			else if (shapeSelect == 3) {
				triangle3[0] += 5;
				triangle3[2] += 5;
				triangle3[4] += 5;

			}
			else if (shapeSelect == 4) {
				triangle4[0] += 5;
				triangle4[2] += 5;
				triangle4[4] += 5;

			}
			break;
		}
		case ofKey::OF_KEY_F3: {

			if (shapeSelect == 1) {
				triangle1[2] = triangle1[2] * scaling + triangle1[0] * (1 - scaling);
				triangle1[3] = triangle1[3] * scaling + triangle1[1] * (1 - scaling);
				triangle1[4] = triangle1[4] * scaling + triangle1[0] * (1 - scaling);
				triangle1[5] = triangle1[5] * scaling + triangle1[1] * (1 - scaling);
			}
			else if (shapeSelect == 2) {
				triangle2[2] = triangle2[2] * scaling + triangle2[0] * (1 - scaling);
				triangle2[3] = triangle2[3] * scaling + triangle2[1] * (1 - scaling);
				triangle2[4] = triangle2[4] * scaling + triangle2[0] * (1 - scaling);
				triangle2[5] = triangle2[5] * scaling + triangle2[1] * (1 - scaling);
			}
			else if (shapeSelect == 3) {
				triangle3[2] = triangle3[2] * scaling + triangle3[0] * (1 - scaling);
				triangle3[3] = triangle3[3] * scaling + triangle3[1] * (1 - scaling);
				triangle3[4] = triangle3[4] * scaling + triangle3[0] * (1 - scaling);
				triangle3[5] = triangle3[5] * scaling + triangle3[1] * (1 - scaling);
			}
			else if (shapeSelect == 4) {
				triangle4[2] = triangle4[2] * scaling + triangle4[0] * (1 - scaling);
				triangle4[3] = triangle4[3] * scaling + triangle4[1] * (1 - scaling);
				triangle4[4] = triangle4[4] * scaling + triangle4[0] * (1 - scaling);
				triangle4[5] = triangle4[5] * scaling + triangle4[1] * (1 - scaling);
			}

			break;
		}
		case ofKey::OF_KEY_F4: {
		
	
			if (shapeSelect == 1) {
				triangle1[2] = triangle1[2] / scaling + triangle1[0] / (1 - scaling);
				triangle1[3] = triangle1[3] / scaling + triangle1[1] / (1 - scaling);
				triangle1[4] = triangle1[4] / scaling + triangle1[0] / (1 - scaling);
				triangle1[5] = triangle1[5] / scaling + triangle1[1] / (1 - scaling);
			}
			else if (shapeSelect == 2) {
				triangle2[2] = triangle2[2] / scaling + triangle2[0] / (1 - scaling);
				triangle2[3] = triangle2[3] / scaling + triangle2[1] / (1 - scaling);
				triangle2[4] = triangle2[4] / scaling + triangle2[0] / (1 - scaling);
				triangle2[5] = triangle2[5] / scaling + triangle2[1] / (1 - scaling);
			}
			else if (shapeSelect == 3) {
				triangle3[2] = triangle3[2] / scaling + triangle3[0] / (1 - scaling);
				triangle3[3] = triangle3[3] / scaling + triangle3[1] / (1 - scaling);
				triangle3[4] = triangle3[4] / scaling + triangle3[0] / (1 - scaling);
				triangle3[5] = triangle3[5] / scaling + triangle3[1] / (1 - scaling);
			}
			else if (shapeSelect == 4) {
				triangle4[2] = triangle4[2] / scaling + triangle4[0] / (1 - scaling);
				triangle4[3] = triangle4[3] / scaling + triangle4[1] / (1 - scaling);
				triangle4[4] = triangle4[4] / scaling + triangle4[0] / (1 - scaling);
				triangle4[5] = triangle4[5] / scaling + triangle4[1] / (1 - scaling);
			}
			
			break;
		}
		case ofKey::OF_KEY_BACKSPACE: {
			if (shapeSelect == 1) {
				NuevoPx1 = triangle1[0];
				triangle1[0] = triangle1[0] * (angC)-triangle1[1] * (angT);
				triangle1[1] = (NuevoPx1 *(angT)+triangle1[1] * (angC));

				NuevoPx2 = triangle1[2];
				triangle1[2] = triangle1[2] * (angC)-triangle1[3] * (angT);
				triangle1[3] = (NuevoPx2 *(angT)+triangle1[3] * (angC));

				NuevoPx3 = triangle1[4];
				triangle1[4] = triangle1[4] * (angC)-triangle1[5] * (angT);
				triangle1[5] = (NuevoPx3 *(angT)+triangle1[5] * (angC));
			}
			if (shapeSelect == 2) {
				NuevoPx1 = triangle2[0];
				triangle2[0] = triangle2[0] * (angC)-triangle2[1] * (angT);
				triangle2[1] = (NuevoPx1 *(angT)+triangle2[1] * (angC));

				NuevoPx2 = triangle2[2];
				triangle2[2] = triangle2[2] * (angC)-triangle2[3] * (angT);
				triangle2[3] = (NuevoPx2 *(angT)+triangle2[3] * (angC));

				NuevoPx3 = triangle2[4];
				triangle2[4] = triangle2[4] * (angC)-triangle2[5] * (angT);
				triangle2[5] = (NuevoPx3 *(angT)+triangle2[5] * (angC));
			}
			if (shapeSelect == 3) {
				NuevoPx1 = triangle3[0];
				triangle3[0] = triangle3[0] * (angC)-triangle3[1] * (angT);
				triangle3[1] = (NuevoPx1 *(angT)+triangle3[1] * (angC));

				NuevoPx2 = triangle3[2];
				triangle3[2] = triangle3[2] * (angC)-triangle3[3] * (angT);
				triangle3[3] = (NuevoPx2 *(angT)+triangle3[3] * (angC));

				NuevoPx3 = triangle3[4];
				triangle3[4] = triangle3[4] * (angC)-triangle3[5] * (angT);
				triangle3[5] = (NuevoPx3 *(angT)+triangle3[5] * (angC));
			}
			if (shapeSelect == 4) {
				NuevoPx1 = triangle4[0];
				triangle4[0] = triangle4[0] * (angC)-triangle4[1] * (angT);
				triangle4[1] = (NuevoPx1 *(angT)+triangle4[1] * (angC));

				NuevoPx2 = triangle4[2];
				triangle4[2] = triangle4[2] * (angC)-triangle4[3] * (angT);
				triangle4[3] = (NuevoPx2 *(angT)+triangle4[3] * (angC));

				NuevoPx3 = triangle4[4];
				triangle4[4] = triangle4[4] * (angC)-triangle4[5] * (angT);
				triangle4[5] = (NuevoPx3 *(angT)+triangle4[5] * (angC));
			}
			break;
		}
		case ofKey::OF_KEY_ALT: {
			completes = 0;
			break;
		}
		
	}
}

//--------------------------------------------------------------
void ofApp::keyReleased(int key){
	switch (key)
	{
		case ofKey::OF_KEY_CONTROL:
		{

		triangle1b[0] = 0;
		triangle1b[1] = 0;
		triangle1b[2] = 0;
		triangle1b[3] = 0;
		triangle1b[4] = 0;
		triangle1b[5] = 0;
		triangle2b[0] = 0;
		triangle2b[1] = 0;
		triangle2b[2] = 0;
		triangle2b[3] = 0;
		triangle2b[4] = 0;
		triangle2b[5] = 0;
		triangle3b[0] = 0;
		triangle3b[1] = 0;
		triangle3b[2] = 0;
		triangle3b[3] = 0;
		triangle3b[4] = 0;
		triangle3b[5] = 0;
		triangle4b[0] = 0;
		triangle4b[1] = 0;
		triangle4b[2] = 0;
		triangle4b[3] = 0;
		triangle4b[4] = 0;
		triangle4b[5] = 0;


		break;
		}
		case ofKey::OF_KEY_ALT: {
			for (int i = 0; i <= 5; i++) {
				if (triangle1[i] == triangle1C[i]) {
					completes += 1;
				}
				if (triangle2[i] == triangle2C[i]) {
					completes += 1;
				}
				if (triangle3[i] == triangle3C[i]) {
					completes += 1;
				}
				if (triangle4[i] == triangle4C[i]) {
					completes += 1;
				}
			}
			if (completes == 24) {
				correcto.play();
				cout << "Victoria";
				ofSleepMillis(10000);
				
			}
			else {
				incorrecto.play();
			}
			break;
		}
		
	}


}

//--------------------------------------------------------------
void ofApp::mouseMoved(int x, int y ){

}

//--------------------------------------------------------------
void ofApp::mouseDragged(int x, int y, int button){

}

//--------------------------------------------------------------
void ofApp::mousePressed(int x, int y, int button){

}

//--------------------------------------------------------------
void ofApp::mouseReleased(int x, int y, int button){

}

//--------------------------------------------------------------
void ofApp::mouseEntered(int x, int y){

}

//--------------------------------------------------------------
void ofApp::mouseExited(int x, int y){

}

//--------------------------------------------------------------
void ofApp::windowResized(int w, int h){

}

//--------------------------------------------------------------
void ofApp::gotMessage(ofMessage msg){

}

//--------------------------------------------------------------
void ofApp::dragEvent(ofDragInfo dragInfo){ 

}
