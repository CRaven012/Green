/*Author of tests 1-3: Reece Murray, 18322891*/

package za.ac.sun.cs.green.service.barvinok;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;
import java.util.logging.Level;

import org.apfloat.Apint;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import za.ac.sun.cs.green.EntireSuite;
import za.ac.sun.cs.green.Green;
import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.util.Configuration;
import za.ac.sun.cs.green.store.redis.RedisStore;

public class BarvinokRedisTests {

	public static Green solver = null;
	/* From CountBarvinokTest.java */
	@BeforeClass
	public static void initialize() {	
		solver = new Green();
		Properties props = new Properties();
		props.setProperty("green.services", "count");
		props.setProperty("green.service.count", "(bounder (canonize barvinok))");
		props.setProperty("green.service.count.bounder", "za.ac.sun.cs.green.service.bounder.BounderService");
		props.setProperty("green.service.count.canonize", "za.ac.sun.cs.green.service.canonizer.SATCanonizerService");
		props.setProperty("green.service.count.barvinok",
				"za.ac.sun.cs.green.service.barvinok.CountBarvinokService");
		props.setProperty("green.barvinok.path", EntireSuite.BARVINOK_PATH);
		props.setProperty("green.store", "za.ac.sun.cs.green.store.redis.RedisStore");
	
		Configuration config = new Configuration(solver, props);
		config.configure();
		
	}
	/* From CountBarvinokTest.java */
	@AfterClass
	public static void report() {
		if (solver != null) {
			solver.report();
		}
	}
	/* From CountBarvinokTest.java */
	private void check(Expression expression, Expression parentExpression, Apint expected) {
		Instance p = (parentExpression == null) ? null : new Instance(solver, null, parentExpression);
		Instance i = new Instance(solver, p, expression);
		Object result = i.request("count");
		solver.getLog().log(Level.FINE,"Answer="+(Apint)result);
		assertNotNull(result);
		assertEquals(Apint.class, result.getClass()); 
		assertEquals(expected, result);
		//System.out.println("TRUE");
		System.out.println(solver.getStore().get("KEY *"));
	}
	/* From CountBarvinokTest.java */
	private void check(Expression expression, Apint expected) {
		check(expression, null, expected);
		
	}
	


	
/*	
	* Author: Reece Murray, 18322891
	* Problem:
	*
	* x * 2 -12 < 0
	* x - 3 > 0
 	*
	* count: 	
	* 2
*/
	@Test
	public void test01() {

		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("x", 0, 99);
		IntConstant a = new IntConstant(12);
		Operation o3 = new Operation(Operation.Operator.MUL, x, new IntConstant(2));
		Operation o4 = new Operation(Operation.Operator.SUB, o3, a);
		Operation o6 = new Operation(Operation.Operator.LT, o4, new IntConstant(0));	
		IntConstant b = new IntConstant(3);
		Operation o1 = new Operation(Operation.Operator.SUB, y, b);
		Operation o2 = new Operation(Operation.Operator.GT, o1, new IntConstant(0));
		Operation o5 = new Operation(Operation.Operator.AND, o6, o2);
		check(o5, new Apint(2));
		
		
	}
	@Test
	public void test02() {

		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("x", 0, 99);
		IntConstant a = new IntConstant(12);
		Operation o3 = new Operation(Operation.Operator.MUL, x, new IntConstant(2));
		Operation o4 = new Operation(Operation.Operator.SUB, o3, a);
		Operation o6 = new Operation(Operation.Operator.LT, o4, new IntConstant(0));	
		IntConstant b = new IntConstant(3);
		Operation o1 = new Operation(Operation.Operator.SUB, y, b);
		Operation o2 = new Operation(Operation.Operator.GT, o1, new IntConstant(0));
		Operation o5 = new Operation(Operation.Operator.AND, o6, o2);
		check(o5, new Apint(2));
		
		
	}
	/*
	 * Author: Reece Murray, 18322891
	 * Problem:
	 *
	 *  x + 10 <= 0
	 *  x - 10 >= 0
	 *
	 *  count: 
	 *  0
	 */
	@Test
	public void test03() {
		
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("x", 0, 99);
	
		
		IntConstant a0 = new IntConstant(0);
		Operation o5 = new Operation(Operation.Operator.ADD, x, new IntConstant(10));
		Operation o6 = new Operation(Operation.Operator.LE, o5, a0);
		Operation o8 = new Operation(Operation.Operator.SUB, y, new IntConstant(10));
		Operation o9 = new Operation(Operation.Operator.GE, o8, a0);
		Operation o10 = new Operation(Operation.Operator.AND, o6, o9);
		check(o10, new Apint(0));
	}

@Test
	public void test04() {
		
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("x", 0, 99);
	
		
		IntConstant a0 = new IntConstant(0);
		Operation o5 = new Operation(Operation.Operator.ADD, x, new IntConstant(10));
		Operation o6 = new Operation(Operation.Operator.LE, o5, a0);
		Operation o8 = new Operation(Operation.Operator.SUB, y, new IntConstant(10));
		Operation o9 = new Operation(Operation.Operator.GE, o8, a0);
		Operation o10 = new Operation(Operation.Operator.AND, o6, o9);
		check(o10, new Apint(0));
	}

	/*
	 * Author: Reece Murray, 18322891
	 * Problem:
	 * x * 2 - 64 < 0
	 * x -3 > 0
	 * x + 10 <= 0
	 * x - 22 >= 0
	 *	
	 * Count:
	 * 0
	 */

	@Test
	public void test05() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("x", 0, 99);
		IntConstant a = new IntConstant(64);
		Operation o3 = new Operation(Operation.Operator.MUL, x, new IntConstant(2));
		Operation o13 = new Operation(Operation.Operator.SUB, o3, a);
		Operation o1 = new Operation(Operation.Operator.LT, o13, new IntConstant(0));	
		IntConstant b = new IntConstant(3);
		Operation o22 = new Operation(Operation.Operator.SUB, y, b);
		Operation o2 = new Operation(Operation.Operator.GT, o22, new IntConstant(0));
		Operation o5 = new Operation(Operation.Operator.AND, o1, o2);
		

		IntVariable x2 = new IntVariable("x", 0, 99);
		IntVariable y2 = new IntVariable("x", 0, 99);
	
		
		IntConstant a10 = new IntConstant(10);
		IntConstant a0 = new IntConstant(0);
		Operation o5b = new Operation(Operation.Operator.ADD, x2, a10);
		Operation o6 = new Operation(Operation.Operator.LE, o5b, new IntConstant(0));
		Operation o8 = new Operation(Operation.Operator.SUB, y2, new IntConstant(22));
		Operation o9 = new Operation(Operation.Operator.GE, o8, new IntConstant(0));
		Operation o10 = new Operation(Operation.Operator.AND, o6, o9);
		Operation o11 = new Operation(Operation.Operator.AND, o5, o10);
		check(o11, new Apint(0));
	}
@Test
	public void test06() {
		IntVariable x = new IntVariable("x", 0, 99);
		IntVariable y = new IntVariable("x", 0, 99);
		IntConstant a = new IntConstant(64);
		Operation o3 = new Operation(Operation.Operator.MUL, x, new IntConstant(2));
		Operation o13 = new Operation(Operation.Operator.SUB, o3, a);
		Operation o1 = new Operation(Operation.Operator.LT, o13, new IntConstant(0));	
		IntConstant b = new IntConstant(3);
		Operation o22 = new Operation(Operation.Operator.SUB, y, b);
		Operation o2 = new Operation(Operation.Operator.GT, o22, new IntConstant(0));
		Operation o5 = new Operation(Operation.Operator.AND, o1, o2);
		

		IntVariable x2 = new IntVariable("x", 0, 99);
		IntVariable y2 = new IntVariable("x", 0, 99);
	
		
		IntConstant a10 = new IntConstant(10);
		IntConstant a0 = new IntConstant(0);
		Operation o5b = new Operation(Operation.Operator.ADD, x2, a10);
		Operation o6 = new Operation(Operation.Operator.LE, o5b, new IntConstant(0));
		Operation o8 = new Operation(Operation.Operator.SUB, y2, new IntConstant(22));
		Operation o9 = new Operation(Operation.Operator.GE, o8, new IntConstant(0));
		Operation o10 = new Operation(Operation.Operator.AND, o6, o9);
		Operation o11 = new Operation(Operation.Operator.AND, o5, o10);
		check(o11, new Apint(0));
	}
}
