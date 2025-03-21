import com.neuronrobotics.bowlerstudio.scripting.DownloadManager

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.RoundedCube

CSG base, constraint, basin, channel, ramp, drop, statue_cutout_back, statue_cutout_front, statue_cutout
def name = "StatuetteDrainage"
def rim = 8
def res = 24

def statuette_dl = "https://github.com/JansenSmith/StatuetteDrainage/releases/download/0.1.0/african.woman.part.4.stl"

//File foo = DownloadManager.download("0.1.0", statuette_dl, (long)16, "~/bin", "african.woman.part.4.stl", "african.woman.part.4.stl")
// Load an STL file from a git repo
// Loading a local file also works here
File foo = ScriptingEngine.fileFromGit(
	"https://github.com/JansenSmith/StatuetteDrainage.git",
	"african.woman.part.4.stl");
// Load the STL from the disk and cache it in memory
CSG statuette  = Vitamins.get(foo);

//create a rounded cube
base = new RoundedCube(	105,// X dimention
				125,// Y dimention
				1000//  Z dimention
				)
				.resolution(res+1)
				.cornerRadius(rim-1)// sets the radius of the corner
				.toCSG()// converts it to a CSG to display
				.toZMax()

constraint = new Cube(1000, 1000, 30).toCSG()
				.toZMax()

base = base.intersect(constraint)
				.toZMax()

basin = new RoundedCube(base.totalX-rim*2, base.totalY-rim-80-10, base.totalZ)
				.resolution(res+2)
				.cornerRadius(3)
				.toCSG()
				.toZMax()
				.toZMax(base)
				.toYMax()
				.toYMin(base)
				.movey(-rim)
				.movez(5)
				
channel = new RoundedCube(20+6, base.totalY-rim-80, base.totalZ)
				.resolution(res+3)
				.cornerRadius(3)
				.toCSG()
				.toZMax()
				.toZMax(base)
				.toYMax()
				.toYMin(base)
				.movey(-rim)
				.movez(5)
				
ramp = new RoundedCube(20+6, 52, 1000)
				.resolution(res+4)
				.cornerRadius(3)
				.toCSG()
				.toZMin()
				.toYMax()
				.rotx(15)
				.toZMin()
				.movez(channel.getMinZ())
				.movey(channel.getMinY()+rim/2+1)
				
drop = new RoundedCube(20+6, 20, base.totalZ)
				.resolution(res+5)
				.cornerRadius(3)
				.toCSG()
				.toZMax()
				.toZMax(base)
				.toYMin()
				.toYMax(base)
				.movey(30+5.5)
				.movez(17)
//				.setIsWireFrame(true)

def inner_diam = 89
statue_cutout_back = new Cylinder(95/2, // Radius at the bottom
                      		inner_diam/2, // Radius at the top
                      		5, // Height
                      		(int)res*4 //resolution
                      		).toCSG()//convert to CSG to display
				.scaleToMeasurmentY(90)
				.toZMax()
				.toYMin()
				.toYMax(base)
//				.movey(-90)

statue_cutout_front = statue_cutout_back.movey(-45)
							.scaleToMeasurmentX(statue_cutout_back.getTotalX()+3)
				

statue_cutout = statue_cutout_back
if(statue_cutout_front) {statue_cutout = statue_cutout_back.hull(statue_cutout_front)}
				
				
base = base.difference(basin).difference(channel).difference(ramp).difference(drop)
if(statue_cutout) {base = base.difference(statue_cutout)}

if (base) {
	base = base.setColor(javafx.scene.paint.Color.SALMON)
				.setName(name+"_base")
				.addAssemblyStep(0, new Transform())
//				.setIsWireFrame(true)
				.setManufacturing({ toMfg ->
					return toMfg
							//.roty(180)// fix the orientation
							.toZMin()//move it down to the flat surface
//							.scale(1000)
				})
}

//return base.toZMin()
return statuette
//return [base, drop]
//return [drop.union(ramp)]
//return [base, statue_cutout_back.setIsWireFrame(true)]


