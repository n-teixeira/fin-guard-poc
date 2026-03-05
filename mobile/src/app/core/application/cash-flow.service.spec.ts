import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CashFlowService } from './cash-flow.service';
import { ApiService } from '../infrastructure/api.service';

describe('CashFlowService', () => {
  let service: CashFlowService;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj('ApiService', ['getCashFlow']);
    apiSpy.getCashFlow.and.returnValue(of({ userId: 'u1', balance: 500, totalCredits: 1000, totalDebits: 500 }));

    TestBed.configureTestingModule({
      providers: [CashFlowService, { provide: ApiService, useValue: apiSpy }],
    });
    service = TestBed.inject(CashFlowService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should load cash flow', () => {
    service.loadCashFlow().subscribe();
    expect(apiSpy.getCashFlow).toHaveBeenCalled();
  });
});
