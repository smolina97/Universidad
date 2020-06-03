
#include <iostream>
#include <GL/glut.h>
using namespace std;

int minX, minY, maxX, maxY;
int fstX, fstY, sndX, sndY;

int code1[4] = { 0, 0, 0, 0 };
int code2[4] = { 0, 0, 0, 0 };

bool isReject = false;



int getcode1(int x, int y)
{
	
	if (y > maxY)
	{
		code1[0] = 1;
	}

	if (y < minY)
	{
		code1[1] = 1;
	}

	if (x > maxX)
	{
		code1[2] = 1;
	}

	if (x < minX)
	{
		code1[3] = 1;
	}

	int codeRes1 = code1[0] * 1000 + code1[1] * 100 + code1[2] * 10 + code1[3];

	return codeRes1;
}

int getcode2(int x, int y) 
{
	
	if (y > maxY)
	{
		code2[0] = 1;
	}

	if (y < minY)
	{
		code2[1] = 1;
	}

	if (x > maxX)
	{
		code2[2] = 1;
	}

	if (x < minX)
	{
		code2[3] = 1;
	}

	int codeRes2 = code2[0] * 1000 + code2[1] * 100 + code2[2] * 10 + code2[3];

	return codeRes2;
}

void generateCodeForPoints()
{
	getcode1(fstX, fstY);
	getcode2(sndX, sndY);
}


void drawLine(int x0, int y0, int x1, int y1)
{
	int dx = x1 - x0, dy = y1 - y0;
	int p = 2 * dy - dx;
	int x, y;

	if (x0 > x1)
	{
		x = x1;
		y = y1;
		x1 = x0;
	}
	else
	{
		x = x0;
		y = y0;
	}

	while (x < x1)
	{
		x++;
		if (p < 0)
		{
			p = p + 2 * dy;
		}
		else
		{
			y++;
			p = p + 2 * dy - 2 * dx;
		}

		if (isReject == true)
		{
			glBegin(GL_POINTS);
			glColor3f(1, 1, 1);
			glVertex2i(x, y);
			glEnd();
		}
		else
		{
			glBegin(GL_POINTS);
			glColor3f(1, 1, 1);
			glVertex2i(x, y);
			glEnd();

			if (x >= minX && x <= maxX && y >= minY && y <= maxY)
			{
				glBegin(GL_POINTS);
				glColor3f(0, 0, 1);
				glVertex2i(x, y);
				glEnd();
			}
		}
	}
}

void cohenSuth()
{

	for (int i = 0; i < 4; i++)
	{
		if (code1[i] == 1 && code2[i] == 1)
		{
			isReject = true;

		}
	}

	if (isReject)
	{
		cout << "Both point rejected" << endl;
	}
	else
	{
		cout << "Not rejected" << endl;

	}
	drawLine(fstX, fstY, sndX, sndY);

}


void myInit(void)
{

	glPointSize(3.0);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D(0.0, 640.0, 0.0, 480.0);
}


void myDisplay(void)
{
	glClear(GL_COLOR_BUFFER_BIT);
	glColor3f(1, 1, 1);
	glRecti(minX, minY, maxX, maxY);
	cohenSuth();
	glFlush();
}

int main(int argc, char** argv)
{
	// clipping zone
	minX = 150, minY = 100, maxX = 480, maxY = 390;

	// line not rejec
	//fstX = 0, fstY = 0, sndX = 460, sndY = 400;
	// line inside
	fstX = 200, fstY = 120, sndX = 460, sndY = 300;
	// line out
	fstX = 0, fstY = 0, sndX = 400, sndY = 90;



	generateCodeForPoints();
	cout << "First  point code: " << code1[0] << code1[1] << code1[2] << code1[3] << endl;
	cout << "Second point code: " << code2[0] << code2[1] << code2[2] << code2[3] << endl;

	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
	glutInitWindowSize(640, 480);
	glutInitWindowPosition(100, 150);
	glutCreateWindow("Proyecto 1 clipping");
	glutDisplayFunc(myDisplay);
	myInit();
	glutMainLoop();

}