

/*Author of tests 1-3: Reece Murray, 18322891*/


package za.ac.sun.cs.green.service.z3;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.Green;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.util.Configuration;

public class Z3Test {

	public static Green solver;
	/* From ModelZ3JavaTest.java */
	@BeforeClass
	public static void initialize() {
		solver = new Green();
		Properties props = new Properties();
		props.setProperty("green.services", "model");
		props.setProperty("green.service.model", "(bounder z3java)");
		props.setProperty("green.service.model.bounder", "za.ac.sun.cs.green.service.bounder.BounderService");
				
		props.setProperty("green.service.model.z3java",
				"za.ac.sun.cs.green.service.z3.ModelZ3JavaService");
		Configuration config = new Configuration(solver, props);
		config.configure();
	}
	/* From ModelZ3JavaTest.java */
	@AfterClass
	public static void report() {
		solver.report();
	}
	/* From ModelZ3JavaTest.java */
	private void checkVal(Expression expression, Expression parentExpression, IntVariable var, int low, int high) {
		Instance p = (parentExpression == null) ? null : new Instance(solver, null, parentExpression);
		Instance i = new Instance(solver, p, expression);
		Object result = i.request("model");
		assertNotNull(result);
		@SuppressWarnings("unchecked")
		Map<IntVariable,Object> res = (Map<IntVariable,Object>)result; 
		int value = (Integer) res.get(var);
		System.out.println(" variable " + var + " = " + value + " -> [" + low + "," + high + "]");
		assertTrue(value >= low && value <= high);
	}
	/* From ModelZ3JavaTest.java */
	private void checkModel(Expression expression, IntVariable v, int value, int val2) {
		checkVal(expression, null, v, value, val2);
	}
	/* From ModelZ3JavaTest.java */
	private void checkModelRange(Expression expression, IntVariable v, int low, int high) {
		checkVal(expression, null, v, low, high);
	}

	/*
	Author: Reece Murray, 18322891
	x >= 12
	y < 20
	x && y
	0 &lt;= x &lt;= 99
	*/
	@Test
	public void test01() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntConstant a = new IntConstant(12);
		IntConstant b = new IntConstant(20);
		Operation o1 = new Operation(Operation.Operator.GE, x, a);
		Operation o2 = new Operation(Operation.Operator.LT, y, b);
		Operation o3 = new Operation(Operation.Operator.AND, o1, o2);
		checkModel(o3,x,5, 15);
	}
	
	/*	
	Author: Reece Murray, 18322891
	x == 55
	y < 80
	x && y
	0 &lt;= x &lt;= 60		
		
	*/
	@Test
	public void test02() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntConstant a = new IntConstant(55);
		IntConstant b = new IntConstant(80);
		Operation o1 = new Operation(Operation.Operator.EQ, x, a);
		Operation o2 = new Operation(Operation.Operator.LT, y, b);
		Operation o3 = new Operation(Operation.Operator.AND, o1, o2);
		checkModel(o3,x,0, 60);
	}	
	/*
	Author: Reece Murray, 18322891
	x <= 12
	y >= 3
	o5 = x <= 12 && y >= 3 
	x <= 6
	y >= 4
	o6 = x <= 6 && y >= 4
	o5 && o6

	0 &lt;= x &lt;= 5
	*/
	@Test
	public void test03() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("y", 0, 99);
		IntConstant a = new IntConstant(12);
		IntConstant b = new IntConstant(3);
		IntConstant c = new IntConstant(4);
		IntConstant d = new IntConstant(6);
		Operation o1 = new Operation(Operation.Operator.LE, x, a);
		Operation o2 = new Operation(Operation.Operator.GE, y, b);
		Operation o5 = new Operation(Operation.Operator.AND, o1, o2);
		Operation o3 = new Operation(Operation.Operator.LE, x, c);
		Operation o4 = new Operation(Operation.Operator.GE, y, d);
		Operation o6 = new Operation(Operation.Operator.AND, o3, o4);
		Operation o7 = new Operation(Operation.Operator.AND, o5, o6);
		checkModelRange(o7,x,0,5);
	}
	
	
	
	
	
}
