#include "CustomSphere.h"

void CustomSphere::GetInfo() {
	auto currentPos = this->getPosition();
	this->x = currentPos.x;
	this->y = currentPos.y;
	this->z = currentPos.z;

}

void CustomSphere::draw(int x, int y, int z, int w, int h, int screenW, int screenH) {
	GetInfo();
	ofSetColor(this->myColor);
	ofSpherePrimitive::draw();
}